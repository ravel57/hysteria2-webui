$path = Get-Location
Set-Location "../../WebstormProjects/hysteria-web-ui"

npm install
npm run build

if (Test-Path -Path "./dist/") {
    Copy-Item -Force -Recurse "./dist/*" "$path/src/main/resources/static"
} else {
    Write-Error "Copying error"
    exit 1
}
Set-Location $path