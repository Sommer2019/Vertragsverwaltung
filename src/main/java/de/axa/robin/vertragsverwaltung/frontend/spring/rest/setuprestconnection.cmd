$username = "admin"
$password = "admin"
$pair = "$($username):$($password)"
$encodedCredentials = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes($pair))

$headers = @{
    Authorization = "Basic $encodedCredentials"
    Content-Type = "application/json"
}

$response = Invoke-RestMethod -Uri "http://localhost:8080/login" -Method POST -Headers $headers