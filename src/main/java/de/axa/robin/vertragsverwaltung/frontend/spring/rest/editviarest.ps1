$body = '{"partner": {"geburtsdatum": "1956-06-12"}}'

Invoke-WebRequest -Uri $uri -Method PUT -Headers $headers -Body $body -WebSession $session