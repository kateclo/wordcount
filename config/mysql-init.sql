# create databases
CREATE DATABASE IF NOT EXISTS `wordcount`;

# create wordcount user and grant rights
CREATE USER 'wordcount'@'%' IDENTIFIED BY 'wordcount';
GRANT ALL ON wordcount.* TO 'wordcount'@'%';
GRANT CREATE USER ON *.* TO 'wordcount'@'%';
GRANT GRANT OPTION ON *.* TO 'wordcount'@'%';