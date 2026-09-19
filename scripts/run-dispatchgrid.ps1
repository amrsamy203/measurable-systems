. "$PSScriptRoot\dev-env.ps1"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location (Join-Path $Root "projects\dispatchgrid")
Write-Host "DispatchGrid → http://localhost:8082 (admin@dispatchgrid.demo / password)"
mvn -q spring-boot:run "-Dspring-boot.run.arguments=--server.port=8082"
