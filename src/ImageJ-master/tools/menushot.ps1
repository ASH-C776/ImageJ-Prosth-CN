Add-Type -AssemblyName System.Drawing

$sig = @'
using System;
using System.Runtime.InteropServices;
public class W {
  [DllImport("user32.dll")] public static extern bool GetMenuItemRect(IntPtr h, IntPtr m, uint id, out RECT r);
  [DllImport("user32.dll")] public static extern IntPtr GetMenu(IntPtr h);
  [DllImport("user32.dll")] public static extern int GetMenuItemCount(IntPtr m);
  [DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr h);
  [DllImport("user32.dll")] public static extern bool BringWindowToTop(IntPtr h);
  [DllImport("user32.dll")] public static extern bool SetWindowPos(IntPtr h, IntPtr after, int x, int y, int cx, int cy, uint f);
  [DllImport("user32.dll")] public static extern bool SetCursorPos(int x, int y);
  [DllImport("user32.dll")] public static extern void mouse_event(uint f, uint dx, uint dy, uint d, IntPtr e);
  [DllImport("user32.dll")] public static extern bool GetWindowRect(IntPtr h, out RECT r);
  [StructLayout(LayoutKind.Sequential)] public struct RECT { public int L, T, R, B; }
}
'@
Add-Type -TypeDefinition $sig

$idx = [int]$args[0]
$out = $args[1]

$p = Get-Process java -ErrorAction SilentlyContinue | Where-Object { $_.MainWindowHandle -ne 0 } | Select-Object -First 1
if (-not $p) { Write-Output "NO_WINDOW"; exit 1 }
$h = $p.MainWindowHandle
Write-Output ("hwnd=" + $h)

[void][W]::BringWindowToTop($h)
[void][W]::SetForegroundWindow($h)
[void][W]::SetWindowPos($h, [IntPtr]::new(-1), 0, 0, 0, 0, 0x0001 -bor 0x0002 -bor 0x0040)
Start-Sleep -Milliseconds 900

$menu = [W]::GetMenu($h)
Write-Output ("menu=" + $menu + " count=" + [W]::GetMenuItemCount($menu))

$r = New-Object W+RECT
if (-not [W]::GetMenuItemRect($h, $menu, [uint32]$idx, [ref]$r)) { Write-Output "ITEM_RECT_FAIL"; exit 1 }
$x = [int](($r.L + $r.R) / 2)
$y = [int](($r.T + $r.B) / 2)
Write-Output ("item rect=" + $r.L + "," + $r.T + "," + $r.R + "," + $r.B + " click=" + $x + "," + $y)

$wr = New-Object W+RECT
[void][W]::GetWindowRect($h, [ref]$wr)
Write-Output ("win rect=" + $wr.L + "," + $wr.T + "," + $wr.R + "," + $wr.B)

[void][W]::SetCursorPos($x, $y)
Start-Sleep -Milliseconds 300
[W]::mouse_event(0x0002, 0, 0, 0, [IntPtr]::Zero)
Start-Sleep -Milliseconds 60
[W]::mouse_event(0x0004, 0, 0, 0, [IntPtr]::Zero)
Start-Sleep -Milliseconds 1200

$bmp = New-Object System.Drawing.Bitmap 2560, 1440
$g = [System.Drawing.Graphics]::FromImage($bmp)
$g.CopyFromScreen(0, 0, 0, 0, $bmp.Size)
$bmp.Save($out, [System.Drawing.Imaging.ImageFormat]::Png)
$g.Dispose(); $bmp.Dispose()
Write-Output ("SAVED " + $out)
