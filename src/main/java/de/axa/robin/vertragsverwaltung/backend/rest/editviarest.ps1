$uri = "http://localhost:8080/api/vertrage/10000000"
$body = '{"partner": {"geburtsdatum": "1956-06-12"}}'

Invoke-WebRequest -Uri $uri -Method PUT -Headers $headers -Body $body -WebSession $session

$uri = "http://localhost:8080/api/vertrage"
Invoke-WebRequest -Uri $uri -Method GET -Headers $headers -WebSession $session

Invoke-WebRequest -Uri $uri -Method GET -Headers $headers -WebSession $session

Invoke-WebRequest -Uri $uri -Method DELETE -Headers $headers -WebSession $session