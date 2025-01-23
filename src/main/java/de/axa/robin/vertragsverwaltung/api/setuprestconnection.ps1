$loginPage = Invoke-WebRequest -Uri "http://localhost:8080/"
$csrfToken = $loginPage.InputFields.Where({ $_.Name -eq '_csrf' }).Value

$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$loginResponse = Invoke-WebRequest -Uri "http://localhost:8080/login" -Method POST -WebSession $session -Body @{
    username = "admin"
    password = "admin"
    _csrf = $csrfToken
}

$headers = @{
    "Content-Type" = "application/json"
    "X-CSRF-TOKEN" = $csrfToken
}
