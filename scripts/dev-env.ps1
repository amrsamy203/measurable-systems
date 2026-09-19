# Development environment bootstrap (Windows PowerShell)
# Usage: . .\scripts\dev-env.ps1

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot

$jdkCandidates = @(
  "C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot",
  "C:\Program Files\Eclipse Adoptium\jdk-21*",
  "C:\Program Files\Java\jdk-21*"
)

$jdk = $null
foreach ($c in $jdkCandidates) {
  $resolved = Get-Item $c -ErrorAction SilentlyContinue | Select-Object -First 1
  if ($resolved) { $jdk = $resolved.FullName; break }
}

if (-not $jdk) {
  Write-Error "Java 21 not found. Install Microsoft OpenJDK 21 or Temurin 21."
}

$mvnHome = Join-Path $Root ".tools\apache-maven-3.9.9"
if (-not (Test-Path (Join-Path $mvnHome "bin\mvn.cmd"))) {
  Write-Error "Maven not found at $mvnHome. Re-run toolchain setup."
}

$env:JAVA_HOME = $jdk
$gitBin = "C:\Program Files\Git\cmd"
$nodeBin = "C:\Program Files\nodejs"
$env:Path = "$jdk\bin;$mvnHome\bin;$gitBin;$nodeBin;" + $env:Path

Write-Host "JAVA_HOME=$env:JAVA_HOME"
java -version
mvn -version | Select-Object -First 1
if (Get-Command node -ErrorAction SilentlyContinue) { Write-Host "Node $(node -v)" }
if (Get-Command git -ErrorAction SilentlyContinue) { Write-Host "Git $(git --version)" }
Write-Host "Environment ready."
