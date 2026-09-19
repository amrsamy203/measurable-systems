. "$PSScriptRoot\dev-env.ps1"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location (Join-Path $Root "projects\caseflow")
$env:SERVER_PORT = "8081"
Write-Host "CaseFlow → http://localhost:8081 (admin@caseflow.demo / password)"
mvn -q spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
