INSERT INTO role(id, role) VALUES (1, 'ADMINISTRATOR');

INSERT INTO users(id, first_name, last_name, email, password) VALUES (1, 'Admin', 'Adminovic', 'admin@uns.ac.rs', 'adminspassword');

INSERT INTO users_roles (users_id, roles_id) VALUES (1, 1);