/*
 * Author: Ovidiu Rosu <rosu.ovi@gmail.com>
 * Description: Added a new table for the Filter class
 */
CREATE TABLE filter(
  filterid INTEGER NOT NULL,
  name VARCHAR(160) UNIQUE NOT NULL,
  shortname VARCHAR(50) UNIQUE NOT NULL,
  uid VARCHAR(11) UNIQUE,
  code VARCHAR(100) UNIQUE,
  description TEXT,
  created TIMESTAMP WITHOUT TIME ZONE,
  lastupdated TIMESTAMP WITHOUT TIME ZONE,
  publicaccess VARCHAR(8),
  userid INTEGER,
  url VARCHAR(255),

  metadatauids TEXT,

  PRIMARY KEY(filterid),
  FOREIGN KEY(userid) REFERENCES userinfo(userinfoid)
);