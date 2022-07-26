-- MySQL dump 10.17  Distrib 10.3.22-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: lucrumea
-- ------------------------------------------------------
-- Server version	10.3.22-MariaDB-0ubuntu0.19.10.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `domains`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `domains` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(2048) NOT NULL,
  `domain` varchar(1024) NOT NULL,
  `domain_token` varchar(1024) NOT NULL,
  `active` bit(1) NOT NULL DEFAULT 0,
  `mainpage` varchar(2048) NOT NULL,
  `signinpage` varchar(2048) NOT NULL,
  `signuppage` varchar(2048) NULL,
  `enable_web_registration` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `groups`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `GROUPNAME` varchar(40) DEFAULT NULL,
  `GROUPDESC` varchar(255) DEFAULT NULL,
  `OWNERUSERID` int(11) DEFAULT NULL,
  `MANDATORY` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `linked_groups`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `linked_groups` (
  `ID` int(11) NOT NULL,
  `GROUPID` int(11) DEFAULT NULL,
  `USERID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `menu_categories`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `menu_categories` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `icon` varchar(256) NOT NULL,
  `has_sub` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `menu_items`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `menu_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cat_id` int(11) NOT NULL,
  `name` varchar(256) NOT NULL,
  `role_id` int(11) NOT NULL,
  `link` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`)
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `npt_users`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `npt_users` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `USERNAME` varchar(40) NOT NULL,
  `PASSWORD` varchar(40) NOT NULL,
  `domain_token` varchar(1024) NOT NULL,
  `MDESC` varchar(255) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `GROUPID` int(11) DEFAULT NULL,
  `ROLESETID` int(11) DEFAULT NULL,
  `DEF_PAGE` varchar(255) DEFAULT NULL,
  `CATEGORY` int(11) DEFAULT NULL,
  `APPROVE_CODE` varchar(18) DEFAULT NULL,
  `EMAIL_ADDRESS` varchar(512) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `info` blob DEFAULT NULL,
  PRIMARY KEY (`ID`)
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rolelist`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rolelist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rolename` varchar(255) DEFAULT NULL,
  `roledesc` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rolesets`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rolesets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `SETID` int(11) DEFAULT NULL,
  `ROLEID` int(11) DEFAULT NULL,
  `ROLESETNAME` varchar(255) DEFAULT NULL,
  `ROLESETDESC` varchar(255) DEFAULT NULL,
  `OWNERUSERID` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-02-21 21:56:21
