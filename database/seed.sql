-- Seed roles
INSERT INTO roles(name) VALUES ('ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO roles(name) VALUES ('CHEF_PROJET') ON CONFLICT DO NOTHING;
INSERT INTO roles(name) VALUES ('MEMBRE') ON CONFLICT DO NOTHING;

-- Users: password placeholders (to be hashed by app on register). For demo, keep plain and let auth-service rehash if needed.
INSERT INTO users(name,email,password,role) VALUES
 ('Admin','admin@pmip.local','$2a$10$3JQv7lM7.Y1Y0xQv8yQ2Du2r8Tz3aZVZbHk2N7mGZ4o5oDkQm41gK','ADMIN')
ON CONFLICT DO NOTHING;

INSERT INTO users(name,email,password,role) VALUES
 ('Chef One','chef1@pmip.local','changeme','CHEF_PROJET'),
 ('Chef Two','chef2@pmip.local','changeme','CHEF_PROJET')
ON CONFLICT DO NOTHING;

INSERT INTO users(name,email,password,role) VALUES
 ('Member A','m1@pmip.local','changeme','MEMBRE'),
 ('Member B','m2@pmip.local','changeme','MEMBRE'),
 ('Member C','m3@pmip.local','changeme','MEMBRE'),
 ('Member D','m4@pmip.local','changeme','MEMBRE'),
 ('Member E','m5@pmip.local','changeme','MEMBRE')
ON CONFLICT DO NOTHING;

-- Projects
INSERT INTO projects(title,description,budget,start_date,end_date,status,progress,manager_id) VALUES
 ('Migration ERP','Migration de l\'ERP vers version 12',50000,'2025-01-01','2025-06-30','EN_COURS',35,2),
 ('Application Mobile','Nouveau produit mobile',120000,'2025-02-15','2025-09-30','PLANIFIE',10,3),
 ('BI Roadmap','Mise en place BI',80000,'2025-03-01','2025-12-15','EN_COURS',20,2)
ON CONFLICT DO NOTHING;

-- Tasks (10)
INSERT INTO tasks(project_id,title,description,priority,status,estimated_duration,due_date,assignee_id) VALUES
 (1,'Audit existant','Analyser modules actuels','ELEVEE','IN_PROGRESS',40,'2025-02-15',4),
 (1,'Plan de migration','Définir stratégie','MOYENNE','TODO',24,'2025-03-10',5),
 (1,'PoC technique','Valider compatibilité','CRITIQUE','IN_PROGRESS',60,'2025-04-01',6),
 (2,'Design UI','Maquettes écrans','MOYENNE','TODO',32,'2025-04-05',7),
 (2,'API Auth','JWT sécurisée','ELEVEE','TODO',28,'2025-04-20',8),
 (2,'Sync offline','Gestion offline','CRITIQUE','TODO',80,'2025-05-10',4),
 (3,'Data Warehouse','Modélisation','MOYENNE','IN_PROGRESS',72,'2025-06-01',5),
 (3,'ETL','Pipelines','ELEVEE','TODO',64,'2025-07-01',6),
 (3,'Dashboards','KPIs','MOYENNE','TODO',48,'2025-08-01',7),
 (1,'Tests UAT','Recette utilisateur','MOYENNE','TODO',56,'2025-06-15',8);
