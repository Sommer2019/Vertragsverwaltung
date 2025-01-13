$body = '{"partner": {"geburtsdatum": "1956-06-12"}}'

Invoke-RestMethod -Uri "http://localhost:8080/api/vertrage/10000000" -Method PUT -Headers $headers -Body $body