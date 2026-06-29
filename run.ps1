Get-Content .env | ForEach-Object {
    if ($_ -match "^(.*?)=(.*)$") {
        Set-Item -Path "env:$($matches[1])" -Value $matches[2]
    }
}
$env:MAVEN_OPTS="-Djava.net.preferIPv4Stack=true"
./mvnw spring-boot:run
