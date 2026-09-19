# Redeploy static portfolio to Vercel (temporary or claimed)

. "$PSScriptRoot\dev-env.ps1"
$env:ComSpec = "C:\Windows\System32\cmd.exe"
$env:Path = "C:\Windows\System32;C:\Windows;C:\Program Files\nodejs;" + $env:Path

$Root = Split-Path -Parent $PSScriptRoot
Set-Location (Join-Path $Root "portfolio-site")
npm run build
Set-Location (Join-Path $Root "portfolio-site\out")
npx --yes vercel deploy --temporary --yes
Write-Host "Claim/keep the URL printed above, then update docs/freelancing/live-links.md"
