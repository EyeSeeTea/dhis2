/*
 * Author: Ovidiu Rosu <rosu.ovi@gmail.com>
 * Description: Added a new table for the Filter class
 */
CREATE TABLE filter(
filterid INTEGER,
name VARCHAR(160),
shortname VARCHAR(50),
uid VARCHAR(11),
code VARCHAR(100),
description TEXT,
created TIMESTAMP WITHOUT TIME ZONE,
lastupdated TIMESTAMP WITHOUT TIME ZONE,
publicaccess VARCHAR(8),
userid INTEGER,
url VARCHAR(255)
);