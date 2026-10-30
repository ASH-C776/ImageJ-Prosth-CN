param([int]$x = 0, [int]$y = 0, [int]$w = 980, [int]$h = 560)

Add-Type -TypeDefinition @'
using System;
using System.Runtime.InteropServices;
public class Mv {
  [DllImport("user32.dll")] public static extern bool GetWindowRect(IntPtr h, out RECT r);
  [DllImport("user32.dll")] public static extern bool SetWindowPos(IntPtr h, IntPtr after, int x, int y, int cx, int cy, uint f);
  [DllImport("user32.dll")] public static extern bool BringWindowToTop(IntPtr h);
  [DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr h);
  [StructLayout(LayoutKind.Sequential)] public struct RECT { public int L, T, R, B; }
}
'@

$p = Get-Process ImageJ-Prosth-CN -ErrorAction SilentlyContinue | Where-Object { $_.MainWindowHandle -ne 0 } | Select-Object -First 1
if (-not $p) { Write-Output "NO_WINDOW"; exit 1 }
$h2 = $p.MainWindowHandle
[void][Mv]::SetWindowPos($h2, [IntPtr]::new(-1), $x, $y, $w, $h, 0x0040)
[void][Mv]::BringWindowToTop($h2)
[void][Mv]::SetForegroundWindow($h2)
Start-Sleep -Milliseconds 600
$r = New-Object Mv+RECT
[void][Mv]::GetWindowRect($h2, [ref]$r)
Write-Output ("win=" + $r.L + "," + $r.T + "," + $r.R + "," + $r.B)
