. "$PSScriptRoot\dev-env.ps1"
$Root = Split-Path -Parent $PSScriptRoot
Set-Location (Join-Path $Root "projects\relateai")
Write-Host "RelateAI → http://localhost:8083 (user1@relateai.demo / password)"
mvn -q spring-boot:run "-Dspring-boot.run.arguments=--server.port=8083"
