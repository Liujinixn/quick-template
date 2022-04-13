/*
Navicat MySQL Data Transfer

Source Server         : 47.102.144.221 - ark123
Source Server Version : 50736
Source Host           : 47.102.144.221:3306
Source Database       : quick-log

Target Server Type    : MYSQL
Target Server Version : 50736
File Encoding         : 65001

Date: 2022-04-13 23:19:38
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_operate_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operate_log`;
CREATE TABLE `sys_operate_log` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `operate_log_id` varchar(20) DEFAULT NULL COMMENT '操作日志ID',
  `url` varchar(255) DEFAULT NULL COMMENT '请求路径',
  `description` varchar(100) DEFAULT NULL COMMENT '请求描述',
  `request_type` varchar(10) DEFAULT NULL COMMENT '请求类型',
  `request_content_type` varchar(255) DEFAULT NULL COMMENT '内容类型（描述入参类型）',
  `request_params` text COMMENT '入参内容（params入参 和 json入参，全部转json格式记录）',
  `response_content_type` varchar(400) DEFAULT NULL COMMENT '响应内容类型',
  `response_params` mediumtext COMMENT '出参内容',
  `client_ip` varchar(20) DEFAULT NULL COMMENT '客户端IP（访问IP）',
  `user_agent` varchar(255) DEFAULT NULL COMMENT '用户代理',
  `consuming_time` int(20) unsigned DEFAULT NULL COMMENT '耗时 单位ms',
  `operating_account` varchar(50) DEFAULT NULL COMMENT '操作人名称',
  `create_time` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=241 DEFAULT CHARSET=utf8;
