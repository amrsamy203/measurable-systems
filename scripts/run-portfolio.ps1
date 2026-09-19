. "$PSScriptRoot\dev-env.ps1"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location (Join-Path $Root "portfolio-site")
if (-not (Test-Path "node_modules")) { npm install }
Write-Host "Portfolio → http://localhost:3000"
npm run dev
