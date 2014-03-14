-- created 2014-03-05

-- This script creates the tables required for integrating asset tag with mt wilson

CREATE  TABLE `mw_as`.`mw_tag_tpm_password` (
  `id` BINARY(16) NOT NULL ,
  `password` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`id`) );
  
CREATE  TABLE `mw_as`.`mw_tag_kvattribute` (
  `id` BINARY(16) NOT NULL ,
  `name` VARCHAR(255) NOT NULL ,
  `value` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`id`) );
 
CREATE  TABLE `mw_as`.`mw_tag_selection` (
  `id` BINARY(16) NOT NULL ,
  `name` VARCHAR(255) NOT NULL ,
  `description` TEXT NULL,
  PRIMARY KEY (`id`) );
  
CREATE  TABLE `mw_as`.`mw_tag_selection_kvattribute` (
  `id` BINARY(16) NOT NULL ,
  `selectionId` BINARY(16) NOT NULL ,
  `kvAttributeId` BINARY(16) NOT NULL ,
  PRIMARY KEY (`id`) );
  
CREATE  TABLE `mw_as`.`mw_tag_certificate` (
  `id` BINARY(16) NOT NULL ,
  `certificate` BLOB NOT NULL ,
  `sha1` CHAR(40) NOT NULL ,
  `sha256` CHAR(64) NOT NULL ,
  `subject` VARCHAR(255) NOT NULL ,
  `issuer` VARCHAR(255) NOT NULL ,
  `notBefore` DATETIME NOT NULL ,
  `notAfter` DATETIME NOT NULL ,
  `revoked` BOOLEAN NOT NULL DEFAULT FALSE ,
  PRIMARY KEY (`id`) );
  
  CREATE  TABLE `mw_as`.`mw_tag_certificate_request` (
  `id` BINARY(16) NOT NULL ,
  `subject` VARCHAR(255) NOT NULL ,
  `selectionId` BINARY(16) NOT NULL ,
  `certificateId` BINARY(16) NULL , 
  `authorityName` VARCHAR(255) NULL ,
  `status` VARCHAR(255) NULL , 
  PRIMARY KEY (`id`) );
  
  CREATE  TABLE `mw_as`.`mw_tag_configuration` (
  `id` BINARY(16) NOT NULL ,
  `name` VARCHAR(255) NOT NULL ,
  `content` BLOB NULL ,
  PRIMARY KEY (`id`) );
  
INSERT INTO `mw_changelog` (`ID`, `APPLIED_AT`, `DESCRIPTION`) VALUES (20140305150000,NOW(),'Patch for creating the tables for migrating asset tag to mtwilson database.');