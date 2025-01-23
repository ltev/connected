CREATE TABLE `users` (
      id bigint NOT NULL AUTO_INCREMENT,
      `username` varchar(50) NOT NULL,
      `password` varchar(68) NOT NULL,
      `enabled` tinyint NOT NULL,
      PRIMARY KEY (id),
      UNIQUE KEY `users_username_idx_1` (username)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `roles` (
      `id` int NOT NULL AUTO_INCREMENT,
      `name` varchar(50) NOT NULL,
      PRIMARY KEY (id),
      UNIQUE KEY roles_name_idx_2 (name)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE users_roles (
      user_id bigint NOT NULL,
      role_id int NOT NULL,
      UNIQUE KEY `users_roles_idx_2` (user_id, role_id),
      CONSTRAINT `users_roles_user_id_fk` FOREIGN KEY (user_id) REFERENCES users (id),
      CONSTRAINT `users_roles_role_id_fk` FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO roles (name) VALUES ('ROLE_USER');