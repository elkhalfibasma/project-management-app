# Kafka local (sans Docker)

1. Télécharger Kafka binaire: https://kafka.apache.org/downloads
2. Dézipper et définir `KAFKA_HOME` vers le dossier.
3. Démarrer ZooKeeper (Kafka 3.5-):
   - `powershell -NoLogo -ExecutionPolicy Bypass -Command "$env:KAFKA_HOME\bin\windows\zookeeper-server-start.bat $env:KAFKA_HOME\config\zookeeper.properties"`
4. Démarrer Kafka broker:
   - `powershell -NoLogo -ExecutionPolicy Bypass -Command "$env:KAFKA_HOME\bin\windows\kafka-server-start.bat $env:KAFKA_HOME\config\server.properties"`
5. Créer les topics:
   - `powershell -ExecutionPolicy Bypass -File create-topics.ps1 -Bootstrap localhost:9092`

Topics: `project-events`, `task-events`, `user-events`, `ai-events`.
