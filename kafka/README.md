# Kafka local (sans Docker)

Ce guide utilise Kafka en mode KRaft (sans ZooKeeper). Testé sur Windows avec Kafka 3.6+.

## Préparation
- Télécharger Kafka binaire: https://kafka.apache.org/downloads (choisir 3.6+)
- Dézipper et définir la variable d’environnement `KAFKA_HOME` vers le dossier (ex: `C:\kafka\kafka_2.13-3.6.1`).

Dans un terminal PowerShell:
- Optionnel: ` $env:KAFKA_HOME = 'C:\\kafka\\kafka_2.13-3.6.1' ` (pour la session en cours)

## Configuration KRaft minimale
Utiliser le fichier d’exemple `config\kraft\server.properties` fourni par Kafka. Ajuster (si nécessaire):

```
process.roles=broker,controller
node.id=1
controller.listener.names=CONTROLLER
listeners=PLAINTEXT://:9092,CONTROLLER://:9093
advertised.listeners=PLAINTEXT://localhost:9092
controller.quorum.voters=1@localhost:9093
log.dirs=./data/kraft-combined-logs
# optionnel pour dev
auto.create.topics.enable=true
offsets.topic.replication.factor=1
transaction.state.log.replication.factor=1
transaction.state.log.min.isr=1
min.insync.replicas=1
```

## Initialisation du cluster (une seule fois)
1) Générer un Cluster ID
```
$id = & "$env:KAFKA_HOME\bin\windows\kafka-storage.bat" random-uuid
echo $id
```
2) Formater le storage avec ce Cluster ID
```
& "$env:KAFKA_HOME\bin\windows\kafka-storage.bat" format -t $id -c "$env:KAFKA_HOME\config\kraft\server.properties"
```

## Démarrer le broker (KRaft)
Dans un nouveau terminal:
```
& "$env:KAFKA_HOME\bin\windows\kafka-server-start.bat" "$env:KAFKA_HOME\config\kraft\server.properties"
```

## Créer les topics
Dans un autre terminal, au chemin de ce repo `kafka/`:
```
powershell -ExecutionPolicy Bypass -File create-topics.ps1 -Bootstrap localhost:9092
```
Topics créés: `project-events`, `task-events`, `user-events`, `ai-events`.

## Vérification
- Lister les topics:
```
& "$env:KAFKA_HOME\bin\windows\kafka-topics.bat" --list --bootstrap-server localhost:9092
```
- Vérifier la version broker:
```
& "$env:KAFKA_HOME\bin\windows\kafka-broker-api-versions.bat" --bootstrap-server localhost:9092
```
- Vérifier que le port 9092 écoute:
```
netstat -ano | findstr :9092
```

## Arrêt
- Arrêter le broker: Ctrl+C dans la fenêtre où il tourne.
- Si vous modifiez `server.properties`, reformatez en relançant l’étape “Initialisation du cluster”.
