CREATE DATABASE IF NOT EXISTS oomall_log DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

CREATE USER 'dbuser'@'localhost' IDENTIFIED BY '123456';
CREATE USER 'dbuser'@'%' IDENTIFIED BY '123456';

GRANT ALL ON oomall_log.* TO 'dbuser'@'localhost';
GRANT ALL ON oomall_log.* TO 'dbuser'@'%';

FLUSH PRIVILEGES;