-- Insert admin user
-- Password is 'Admin@123' (bcrypted)
INSERT INTO users (name, email, password, roles, selected_role, created_at, updated_at)
VALUES ('Admin', 'admin@gppalanpur.in', '$2a$10$rWZcaYVUcz5Pq9aYsO0c6eqwQKH3dVbDjutkR7tNIECh8h/Z6VlFq', '["admin"]', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);