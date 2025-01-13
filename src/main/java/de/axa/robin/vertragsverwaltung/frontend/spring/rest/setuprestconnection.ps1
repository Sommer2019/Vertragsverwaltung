$loginPage = Invoke-WebRequest -Uri "http://localhost:8080/"
$csrfToken = $loginPage.InputFields.Where({ $_.Name -eq '_csrf' }).Value

$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$loginResponse = Invoke-WebRequest -Uri "http://localhost:8080/login" -Method POST -WebSession $session -Body @{
    username = "admin"
    password = "admin"
    _csrf = $csrfToken
}

$uri = "http://localhost:8080/api/vertrage/10000000"
$headers = @{
    "Content-Type" = "application/json"
    "X-CSRF-TOKEN" = $csrfToken
}
