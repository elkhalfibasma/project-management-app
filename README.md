# ProjectManagementIntelligencePlatform (PMIP)

Exécution locale sans Docker ni CI/CD. Architecture microservices Spring Boot + Angular + Kafka + PostgreSQL + IA FastAPI.

## Arborescence
- backend/ (gateway, auth-service, user-service, project-service, task-service, report-service, ai-service-connector)
- frontend/project-management-ui (Angular)
- ai/prediction-api (FastAPI)
- database/schema.sql, seed.sql
- kafka/ (scripts PowerShell)
- uml/ (*.puml)
- docs/ (cahier_des_charges.pdf à placer ici)

## Prérequis
- Java 17+ (JDK 21/23 OK)
- Maven 3.9+
- Node 18+
- Angular CLI (npm i -g @angular/cli)
- Python 3.10+
- PostgreSQL 14+
- Apache Kafka (binaire local)

## Ordre d’exécution
1. Base de données: créer la base `pmip`, exécuter `database/schema.sql` puis `database/seed.sql`.
2. IA: `cd ai/prediction-api`, `pip install -r requirements.txt`, `python app.py` (lance sur http://localhost:8000).
3. Kafka (KRaft, sans ZooKeeper): voir `kafka/README.md`.
   - Raccourci Windows:
     - `$env:KAFKA_HOME='C:\\kafka\\kafka_2.13-3.6.1'`
     - `$id = & "$env:KAFKA_HOME\bin\windows\kafka-storage.bat" random-uuid; echo $id`
     - `& "$env:KAFKA_HOME\bin\windows\kafka-storage.bat" format -t $id -c "$env:KAFKA_HOME\config\kraft\server.properties"`
     - `& "$env:KAFKA_HOME\bin\windows\kafka-server-start.bat" "$env:KAFKA_HOME\config\kraft\server.properties"`
     - (dans un 2ᵉ terminal, au dossier `kafka/`) `powershell -ExecutionPolicy Bypass -File create-topics.ps1 -Bootstrap localhost:9092`
   - Vérifier: `& "$env:KAFKA_HOME\bin\windows\kafka-topics.bat" --list --bootstrap-server localhost:9092`
4. Microservices (Maven): depuis `backend/` lancer en terminaux séparés:
   - `mvn -DskipTests spring-boot:run -pl auth-service`
   - `mvn -DskipTests spring-boot:run -pl user-service`
   - `mvn -DskipTests spring-boot:run -pl project-service`
   - `mvn -DskipTests spring-boot:run -pl task-service`
   - `mvn -DskipTests spring-boot:run -pl report-service`
   - `mvn -DskipTests spring-boot:run -pl ai-service-connector`
   - `mvn -DskipTests spring-boot:run -pl gateway`
   - Ports: gateway:8080, auth:8081, user:8082, project:8083, task:8084, report:8085, ai-connector:8086
5. Frontend Angular: `cd frontend/project-management-ui`, `npm install`, `npm start` (http://localhost:4200).

## Notes
- JWT secret en dev dans `auth-service` (changez en prod).
- Kafka `bootstrap.servers=localhost:9092`.
- L’`ai-service-connector` appelle http://localhost:8000.
