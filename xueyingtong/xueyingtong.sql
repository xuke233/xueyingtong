/*
Navicat MySQL Data Transfer

Source Server         : 腾讯云
Source Server Version : 50724
Source Host           : 123.206.206.93:3306
Source Database       : xueyingtong

Target Server Type    : MYSQL
Target Server Version : 50724
File Encoding         : 65001

Date: 2019-07-23 15:31:55
*/

create database xueyingtong;
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `xueyingtong`.`file`;
CREATE TABLE `xueyingtong`.`file` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `filename` varchar(255) NOT NULL DEFAULT '',
  `path` varchar(255) NOT NULL DEFAULT '',
  `score` int(11) NOT NULL DEFAULT '0',
  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of file
-- ----------------------------

-- ----------------------------
-- Table structure for filee
-- ----------------------------
DROP TABLE IF EXISTS `xueyingtong`.`filee`;
CREATE TABLE `xueyingtong`.`filee` (
  `id` int(11) NOT NULL,
  `filename` varchar(255) NOT NULL,
  `path` varchar(255) NOT NULL,
  `score` int(11) NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of filee
-- ----------------------------
INSERT INTO `xueyingtong`.`filee` VALUES ('16', 'imac-2x.png', 'upload/1555143130036/imac-2x.png', '94', '2019-04-13 08:12:10');
INSERT INTO `xueyingtong`.`filee` VALUES ('17', 'icon.png', 'upload/1555143244113/icon.png', '72', '2019-04-13 08:14:04');
INSERT INTO `xueyingtong`.`filee` VALUES ('18', 'Registry.db', 'upload/1555143293963/Registry.db', '24', '2019-04-13 08:14:54');
INSERT INTO `xueyingtong`.`filee` VALUES ('19', '123.circ', 'upload/1563866270152/123.circ', '54', '2019-07-23 07:17:50');

-- ----------------------------
-- Table structure for hibernate_sequence
-- ----------------------------
DROP TABLE IF EXISTS `xueyingtong`.`hibernate_sequence`;
CREATE TABLE `xueyingtong`.`hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of hibernate_sequence
-- ----------------------------
INSERT INTO `xueyingtong`.`hibernate_sequence` VALUES ('20');
