CREATE DATABASE  IF NOT EXISTS `iotequ` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `iotequ`;
-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: iotequ
-- ------------------------------------------------------
-- Server version	5.7.17-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `oauth_client_details`
--

DROP TABLE IF EXISTS `oauth_client_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oauth_client_details` (
  `client_id` varchar(255) NOT NULL COMMENT 'client_id',
  `resource_ids` varchar(255) DEFAULT NULL,
  `client_secret` varchar(255) NOT NULL COMMENT 'client_secret',
  `scope` varchar(255) NOT NULL COMMENT 'scope',
  `authorized_grant_types` varchar(255) NOT NULL COMMENT '认证类型',
  `web_server_redirect_uri` varchar(255) DEFAULT NULL COMMENT 'redirect_url',
  `authorities` varchar(255) NOT NULL COMMENT '权限集',
  `access_token_validity` int(11) NOT NULL COMMENT 'token有效时间',
  `refresh_token_validity` int(11) NOT NULL COMMENT '刷新时间秒',
  `additional_information` text COMMENT '附加属性',
  `autoapprove` varchar(20) NOT NULL DEFAULT '1' COMMENT '自动授权',
  `expired_date` datetime DEFAULT NULL COMMENT '过期时间',
  `locked` tinyint(1) NOT NULL DEFAULT '0' COMMENT '锁定',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '激活',
  `decription` varchar(200) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='OAuth2客户端配置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_client_details`
--

LOCK TABLES `oauth_client_details` WRITE;
/*!40000 ALTER TABLE `oauth_client_details` DISABLE KEYS */;
INSERT INTO `oauth_client_details` VALUES ('pay','','123456','api','password,authorization_code,implicit,client_credentials','http://localhost/res/code','ROLE_pay',86400,0,'{\"country\":\"CN\",\"country_code\":\"086\"}','true',NULL,0,1,'支付api调用'),('svas',NULL,'123456','api','client_credentials',NULL,'ROLE_svas',86400,0,NULL,'true',NULL,0,1,NULL);
/*!40000 ALTER TABLE `oauth_client_details` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-09-23 10:27:31
