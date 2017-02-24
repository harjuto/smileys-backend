# smiley schema

# --- !Ups

CREATE TABLE smiley (
  id int(11) NOT NULL AUTO_INCREMENT,
  user_external_id VARCHAR(255) NOT NULL,
  name varchar(255) NOT NULL,
  date datetime NOT NULL,
  image BLOB,
  customer_external_id VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8;

# --- !Downs

DROP TABLE smiley;