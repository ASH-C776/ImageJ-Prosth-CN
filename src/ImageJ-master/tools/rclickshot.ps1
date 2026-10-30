Add-Type -AssemblyName System.Drawing

$sig = @'
using System;
using System.Runtime.InteropServices;
public class W2 {
  [DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr h);
  [DllImport("user32.dll")] public static extern bool BringWindowToTop(IntPtr h);
  [DllImport("user32.dll")] public static extern bool SetWindowPos(IntPtr h, IntPtr after, int x, int y, int cx, int cy, uint f);
  [DllImport("user32.dll")] public static extern bool GetWindowRect(IntPtr h, out RECT r);
  [DllImport("user32.dll")] public static extern bool SetCursorPos(int x, int y);
  [DllImport("user32.dll")] public static extern void mouse_event(uint f, uint dx, uint dy, uint d, IntPtr e);
  [StructLayout(LayoutKind.Sequential)] public struct RECT { public int L, T, R, B; }
}
'@
Add-Type -TypeDefinition $sig

$dx = [int]$args[0]
$dy = [int]$args[1]
$out = $args[2]

$p = Get-Process ImageJ-Prosth-CN -ErrorAction SilentlyContinue | Where-Object { $_.MainWindowHandle -ne 0 } | Select-Object -First 1
if (-not $p) { Write-Output "NO_WINDOW"; exit 1 }
$h = $p.MainWindowHandle
[void][W2]::BringWindowToTop($h)
[void][W2]::SetForegroundWindow($h)
[void][W2]::SetWindowPos($h, [IntPtr]::new(-1), 0, 0, 0, 0, 0x0001 -bor 0x0002 -bor 0x0040)
Start-Sleep -Milliseconds 800

$r = New-Object W2+RECT
[void][W2]::GetWindowRect($h, [ref]$r)
Write-Output ("win=" + $r.L + "," + $r.T + "," + $r.R + "," + $r.B)
$x = $r.L + $dx
$y = $r.T + $dy
Write-Output ("click=" + $x + "," + $y)
[void][W2]::SetCursorPos($x, $y)
Start-Sleep -Milliseconds 250
[W2]::mouse_event(([int]$args[3]), 0, 0, 0, [IntPtr]::Zero)
Start-Sleep -Milliseconds 60
[W2]::mouse_event(0x0004, 0, 0, 0, [IntPtr]::Zero)
Start-Sleep -Milliseconds 1200

$bmp = New-Object System.Drawing.Bitmap 2560, 1440
$g = [System.Drawing.Graphics]::FromImage($bmp)
$g.CopyFromScreen(0, 0, 0, 0, $bmp.Size)
$bmp.Save($out, [System.Drawing.Imaging.ImageFormat]::Png)
$g.Dispose(); $bmp.Dispose()
Write-Output ("SAVED " + $out)
