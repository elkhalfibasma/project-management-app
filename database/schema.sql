-- PMIP schema
CREATE TABLE IF NOT EXISTS roles (
  id SERIAL PRIMARY KEY,
  name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(160) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL,
  created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS projects (
  id SERIAL PRIMARY KEY,
  title VARCHAR(160) NOT NULL,
  description TEXT,
  budget NUMERIC(12,2),
  start_date DATE,
  end_date DATE,
  status VARCHAR(40) DEFAULT 'PLANIFIE',
  progress INTEGER DEFAULT 0,
  manager_id INTEGER,
  CONSTRAINT fk_manager FOREIGN KEY (manager_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS tasks (
  id SERIAL PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
  title VARCHAR(160) NOT NULL,
  description TEXT,
  priority VARCHAR(20) DEFAULT 'MOYENNE',
  status VARCHAR(20) DEFAULT 'TODO',
  estimated_duration INTEGER,
  due_date DATE,
  assignee_id INTEGER REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS project_logs (
  id SERIAL PRIMARY KEY,
  project_id INTEGER REFERENCES projects(id) ON DELETE CASCADE,
  action VARCHAR(120) NOT NULL,
  details TEXT,
  created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS reports (
  id SERIAL PRIMARY KEY,
  project_id INTEGER REFERENCES projects(id) ON DELETE CASCADE,
  data JSONB,
  created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS ai_predictions (
  id SERIAL PRIMARY KEY,
  project_id INTEGER REFERENCES projects(id) ON DELETE CASCADE,
  prediction_type VARCHAR(50) NOT NULL,
  value JSONB NOT NULL,
  created_at TIMESTAMP DEFAULT now()
);
