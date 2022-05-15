/*
Navicat MySQL Data Transfer

Source Server         : 47.102.144.221 - ark123
Source Server Version : 50736
Source Host           : 47.102.144.221:3306
Source Database       : quick-auth

Target Server Type    : MYSQL
Target Server Version : 50736
File Encoding         : 65001

Date: 2022-05-15 17:55:20
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `permission_id` varchar(20) NOT NULL COMMENT '权限id',
  `name` varchar(100) NOT NULL COMMENT '权限名称',
  `description` varchar(255) DEFAULT NULL COMMENT '权限描述',
  `url` varchar(255) DEFAULT NULL COMMENT '权限访问路径',
  `perms` varchar(255) DEFAULT NULL COMMENT '权限标识',
  `parent_id` int(20) DEFAULT NULL COMMENT '父级权限id',
  `type` int(5) DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
  `order_num` int(5) DEFAULT '0' COMMENT '排序',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `status` int(5) NOT NULL COMMENT '状态：1有效；2删除',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `serve_name` varchar(60) DEFAULT NULL COMMENT '所属服务名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COMMENT='权限表';

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES ('1', '1', '工作台', '工作台', '/workbench', 'workbench', '0', '1', '1', 'fa fa-home', '1', '2017-09-27 21:22:02', '2018-02-27 10:53:14', 'quick');
INSERT INTO `sys_permission` VALUES ('2', '2', '系统管理', '系统管理', null, null, '0', '0', '2', 'fa fa-th-list', '1', '2017-07-13 15:04:42', '2018-02-27 10:53:14', 'quick');
INSERT INTO `sys_permission` VALUES ('3', '201', '用户管理', '用户管理', null, null, '2', '1', '1', 'fa fa-circle-o', '1', '2017-07-13 15:05:47', '2021-01-26 00:09:58', 'quick');
INSERT INTO `sys_permission` VALUES ('4', '20101', '列表查询', '用户列表查询', '/auth-server-api/user/list', 'user:list', '3', '2', '0', null, '1', '2017-07-13 15:09:24', '2017-10-09 05:38:29', 'quick');
INSERT INTO `sys_permission` VALUES ('5', '20102', '新增', '新增用户', '/auth-server-api/user/add', 'user:add', '3', '2', '0', null, '1', '2017-07-13 15:06:50', '2018-02-28 17:58:46', 'quick');
INSERT INTO `sys_permission` VALUES ('6', '20103', '编辑', '编辑用户', '/auth-server-api/user/edit', 'user:edit', '3', '2', '0', null, '1', '2017-07-13 15:08:03', '2018-02-27 10:53:14', 'quick');
INSERT INTO `sys_permission` VALUES ('7', '20104', '删除', '删除用户', '/auth-server-api/user/delete', 'user:delete', '3', '2', '0', null, '1', '2017-07-13 15:08:42', '2018-02-27 10:53:14', 'quick');
INSERT INTO `sys_permission` VALUES ('8', '20105', '批量删除', '批量删除用户', '/auth-server-api/user/batch/delete', 'user:batchDelete', '3', '2', '0', null, '1', '2018-07-11 01:53:09', '2018-07-11 01:53:09', 'quick');
INSERT INTO `sys_permission` VALUES ('9', '20106', '分配角色', '分配角色', '/auth-server-api/user/assign/role', 'user:assignRole', '3', '2', '0', null, '1', '2017-07-13 15:09:24', '2017-10-09 05:38:29', 'quick');
INSERT INTO `sys_permission` VALUES ('10', '20107', '踢出用户', '踢出用户', '/auth-server-api/user/online/kickout', 'user:onlineKickout', '3', '2', '0', null, '1', '2018-07-18 21:41:54', '2018-07-19 12:48:25', 'quick');
INSERT INTO `sys_permission` VALUES ('11', '20108', '批量踢出', '批量踢出', '/auth-server-api/user/online/batch/kickout', 'user:onlineBatchKickout', '3', '2', '0', null, '1', '2018-07-19 12:49:30', '2018-07-19 12:49:30', 'quick');
INSERT INTO `sys_permission` VALUES ('12', '20109', '导出用户信息', '导出用户信息', '/auth-server-api/user/export/info/pdf', 'userExportInfoPdf', '3', '2', '0', null, '1', '2018-07-19 12:49:30', '2018-07-19 12:49:30', 'quick');
INSERT INTO `sys_permission` VALUES ('13', '202', '角色管理', '角色管理', null, null, '2', '1', '2', 'fa fa-circle-o', '1', '2017-07-17 14:39:09', '2018-02-27 10:53:14', 'quick');
INSERT INTO `sys_permission` VALUES ('14', '20201', '列表查询', '角色列表查询', '/auth-server-api/role/list', 'role:list', '13', '2', '0', null, '1', '2017-10-10 15:31:36', '2018-02-27 10:53:14', 'quick');
INSERT INTO `sys_permission` VALUES ('15', '20202', '新增', '新增角色', '/auth-server-api/role/add', 'role:add', '13', '2', '0', null, '1', '2017-07-17 14:39:46', '2018-02-27 10:53:14', 'quick');
INSERT INTO `sys_permission` VALUES ('16', '20203', '编辑', '编辑角色', '/auth-server-api/role/edit', 'role:edit', '13', '2', '0', null, '1', '2017-07-17 14:40:15', '2018-02-27 10:53:14', 'quick');
INSERT INTO `sys_permission` VALUES ('17', '20204', '删除', '删除角色', '/auth-server-api/role/delete', 'role:delete', '13', '2', '0', null, '1', '2017-07-17 14:40:57', '2018-02-27 10:53:14', 'quick');
INSERT INTO `sys_permission` VALUES ('18', '20205', '批量删除', '批量删除角色', '/auth-server-api/role/batch/delete', 'role:batchDelete', '13', '2', '0', null, '1', '2018-07-10 22:20:43', '2018-07-10 22:20:43', 'quick');
INSERT INTO `sys_permission` VALUES ('19', '20206', '分配权限', '分配权限', '/auth-server-api/role/assign/permission', 'role:assignPerms', '13', '2', '0', null, '1', '2017-09-26 07:33:05', '2018-02-27 10:53:14', 'quick');
INSERT INTO `sys_permission` VALUES ('20', '203', '资源管理', '资源管理', null, null, '2', '1', '3', 'fa fa-circle-o', '1', '2017-09-26 07:33:51', '2018-02-27 10:53:14', 'quick');
INSERT INTO `sys_permission` VALUES ('21', '20301', '列表查询', '资源列表查询', '/auth-server-api/permission/list', 'permission:list', '20', '2', '0', null, '1', '2018-07-12 16:25:28', '2018-07-12 16:25:33', 'quick');
INSERT INTO `sys_permission` VALUES ('22', '20302', '新增', '新增资源', '/auth-server-api/permission/add', 'permission:add', '20', '2', '0', null, '1', '2017-09-26 08:06:58', '2018-02-27 10:53:14', 'quick');
INSERT INTO `sys_permission` VALUES ('23', '20303', '编辑', '编辑资源', '/auth-server-api/permission/edit', 'permission:edit', '20', '2', '0', null, '1', '2017-09-27 21:29:04', '2018-02-27 10:53:14', 'quick');
INSERT INTO `sys_permission` VALUES ('24', '20304', '删除', '删除资源', '/auth-server-api/permission/delete', 'permission:delete', '20', '2', '0', null, '1', '2017-09-27 21:29:50', '2018-02-27 10:53:14', 'quick');
INSERT INTO `sys_permission` VALUES ('25', '204', '日志管理', '日志管理', null, null, '2', '1', '4', 'fa fa-th-list', '1', '2018-07-06 15:19:26', '2018-07-06 15:19:26', 'quick');
INSERT INTO `sys_permission` VALUES ('26', '20401', '日列表查询', '日志列表查询', '/log-server-api/operateLog/list', 'operateLog:list', '25', '2', '0', null, '1', '2018-07-12 16:25:28', '2018-07-12 16:25:33', 'quick');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `role_id` varchar(20) NOT NULL COMMENT '角色id',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `status` int(5) NOT NULL COMMENT '状态：1有效；2删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `serve_name` varchar(60) DEFAULT NULL COMMENT '所属服务名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '1', '系统管理员', '超级管理员', '1', '2017-06-28 20:30:05', '2017-06-28 20:30:10', 'quick');
INSERT INTO `sys_role` VALUES ('2', '2', '管理员', '管理员', '1', '2017-06-30 23:35:19', '2017-10-11 09:32:33', 'quick');
INSERT INTO `sys_role` VALUES ('3', '3', '普通用户', '普通用户', '1', '2017-06-30 23:35:44', '2018-07-13 11:44:06', 'quick');
INSERT INTO `sys_role` VALUES ('4', '4', '数据库管理员', '数据库管理员', '1', '2017-07-12 11:50:22', '2017-10-09 17:38:02', 'quick');

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `role_id` varchar(20) NOT NULL COMMENT '角色id',
  `permission_id` varchar(20) NOT NULL COMMENT '权限id',
  `serve_name` varchar(60) DEFAULT NULL COMMENT '所属服务名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1114 DEFAULT CHARSET=utf8 COMMENT='角色权限关联表';

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES ('1081', '1', '1', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1082', '1', '2', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1083', '1', '201', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1084', '1', '20101', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1085', '1', '20102', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1086', '1', '20103', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1087', '1', '20104', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1088', '1', '20105', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1089', '1', '20106', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1090', '1', '20107', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1091', '1', '20108', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1092', '1', '20109', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1093', '1', '202', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1094', '1', '20201', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1095', '1', '20202', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1096', '1', '20203', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1097', '1', '20204', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1098', '1', '20205', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1099', '1', '20206', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1100', '1', '203', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1101', '1', '20301', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1102', '1', '20302', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1103', '1', '20303', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1104', '1', '20304', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1105', '1', '204', 'quick');
INSERT INTO `sys_role_permission` VALUES ('1106', '1', '20401', 'quick');

-- ----------------------------
-- Table structure for sys_serve
-- ----------------------------
DROP TABLE IF EXISTS `sys_serve`;
CREATE TABLE `sys_serve` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `serve_id` varchar(20) DEFAULT NULL COMMENT '认证服务ID',
  `serve_name` varchar(60) DEFAULT NULL COMMENT '认证服务名称',
  `access_key` varchar(255) DEFAULT NULL COMMENT '服务密钥',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='认证服务配置表';

-- ----------------------------
-- Records of sys_serve
-- ----------------------------
INSERT INTO `sys_serve` VALUES ('1', '100', 'quick', '4728276AFDC24F82');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` varchar(20) NOT NULL COMMENT '用户id',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(50) NOT NULL,
  `salt` varchar(128) DEFAULT NULL COMMENT '加密盐值',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(50) DEFAULT NULL COMMENT '联系方式',
  `sex` int(5) DEFAULT NULL COMMENT '性别：1男2女',
  `age` int(5) DEFAULT NULL COMMENT '年龄',
  `status` int(5) NOT NULL COMMENT '用户状态：1有效; 2删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `login_ip_address` varchar(50) DEFAULT NULL COMMENT '最后登录的设备IP',
  `head_portrait` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `serve_name` varchar(60) DEFAULT NULL COMMENT '所属服务名称',
  PRIMARY KEY (`id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', '1', 'admin', '43442676c74ae59f219c2d87fd6bad52', null, '523179414@qq.com', '187888899991', '1', '27', '1', '2018-05-23 21:22:06', '2022-04-14 22:29:47', '2022-05-15 09:13:42', '127.0.0.1', 'https://img1.baidu.com/it/u=2381799888,2910666530&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', 'quick');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(20) NOT NULL COMMENT '用户id',
  `role_id` varchar(20) NOT NULL COMMENT '角色id',
  `serve_name` varchar(60) DEFAULT NULL COMMENT '所属服务名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='用户角色关联表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('1', '1', '1', 'quick');
INSERT INTO `sys_user_role` VALUES ('2', '1', '2', 'quick');
