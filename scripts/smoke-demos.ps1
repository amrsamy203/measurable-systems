# Smoke-test all Spring demos (unit tests + brief boot check)
# Usage: .\scripts\smoke-demos.ps1

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\dev-env.ps1"

$projects = @(
  @{ Name = "caseflow"; Dir = "projects\caseflow"; Port = 8081 },
  @{ Name = "dispatchgrid"; Dir = "projects\dispatchgrid"; Port = 8082 },
  @{ Name = "relateai"; Dir = "projects\relateai"; Port = 8083 }
)

$Root = Split-Path -Parent $PSScriptRoot

foreach ($p in $projects) {
  Write-Host "`n===== TEST $($p.Name) =====" -ForegroundColor Cyan
  Push-Location (Join-Path $Root $p.Dir)
  try {
    mvn -B -q test
    if ($LASTEXITCODE -ne 0) { throw "Tests failed for $($p.Name)" }
    Write-Host "OK tests: $($p.Name)" -ForegroundColor Green
  } finally {
    Pop-Location
  }
}

Write-Host "`nAll unit tests passed." -ForegroundColor Green
Write-Host "To run demos locally:"
Write-Host "  .\scripts\run-caseflow.ps1"
Write-Host "  .\scripts\run-dispatchgrid.ps1"
Write-Host "  .\scripts\run-relateai.ps1"
Write-Host "  .\scripts\run-portfolio.ps1"
