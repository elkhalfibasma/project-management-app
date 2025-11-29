param(
  [string]$Bootstrap="localhost:9092"
)

$topics = @(
  "project-events",
  "task-events",
  "user-events",
  "ai-events"
)

if (-not $env:KAFKA_HOME) { Write-Error "KAFKA_HOME non défini. Installez Kafka et définissez KAFKA_HOME."; exit 1 }

foreach ($t in $topics) {
  & "$env:KAFKA_HOME\bin\windows\kafka-topics.bat" --create --if-not-exists --topic $t --bootstrap-server $Bootstrap | Write-Output
}

& "$env:KAFKA_HOME\bin\windows\kafka-topics.bat" --list --bootstrap-server $Bootstrap
