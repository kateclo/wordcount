CREATE USER '${readOnlyUsername}'@'%' IDENTIFIED BY '${readOnlyPassword}';

CREATE TABLE `users`
(
    `username`              VARCHAR(50)  NOT NULL,
    `password`              CHAR(68)  NOT NULL,
    `enabled`               TINYINT NOT NULL,
    PRIMARY KEY (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


CREATE TABLE `authorities`
(
    `username`               VARCHAR(50)  NOT NULL,
    `authority`              CHAR(68)  NOT NULL,
    CONSTRAINT UC_Authorities UNIQUE (`username`, `authority`),
    CONSTRAINT FK_User FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


INSERT INTO `users` VALUES
('epassi', '$2a$10$/Kds1Y3jLBjRrBw/8sOKeOtEpAiqgeZZ//XxZehVn9FftnnobuQD.', 1),
('admin', '$2a$10$tC2MWdj.VzM/c1m03mjHb.eya9swuND8lbEd9odINoIy2uheUoGvm', 1);

INSERT INTO `authorities` VALUES
('epassi', 'ROLE_USER'),
('admin', 'ROLE_USER')
('admin', 'ROLE_ADMIN');