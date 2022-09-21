DROP DATABASE IF EXISTS demo;
CREATE DATABASE IF NOT EXISTS demo DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
USE demo;

-- ----------------------------
-- 0. 用户信息表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`            bigint(16)          NOT NULL AUTO_INCREMENT,
    `user_id`       varchar(16)         NOT NULL                    comment '用户ID',
    `user_name`     varchar(64)         NOT NULL                    comment '用户账号',
    `nick_name`     varchar(64)         NOT NULL                    comment '用户昵称',
    `user_type`     varchar(2)          DEFAULT '00'                comment '用户类型（00系统用户）',
    `email`         varchar(64)         DEFAULT ''                  comment '用户邮箱',
    `phone`         varchar(11)         DEFAULT ''                  comment '手机号码',
    `sex`           char(1)             DEFAULT '0'                 comment '用户性别（0男 1女 2未知）',
    `avatar`        varchar(100)        DEFAULT ''                  comment '头像地址',
    `password`      varchar(100)        DEFAULT ''                  comment '密码',
    `status`        char(1)             DEFAULT '0'                 comment '帐号状态（0正常 1停用）',
    `del_flag`      char(1)             DEFAULT '0'                 comment '删除标志（0代表存在 1代表删除）',
    `login_ip`      varchar(128)        DEFAULT ''                  comment '最后登录IP',
    `login_date`    datetime                                        comment '最后登录时间',
    `create_by`     varchar(64)         DEFAULT ''                  comment '创建者',
    `create_time`   datetime                                        comment '创建时间',
    `update_by`     varchar(64)         DEFAULT ''                  comment '更新者',
    `update_time`   datetime                                        comment '更新时间',
    `remark`        varchar(500)        DEFAULT NULL                comment '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    UNIQUE KEY `uk_uname` (`user_name`),
    KEY `idx_email` (`email`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=1 comment='用户信息表';

-- ----------------------------
-- 初始化-用户信息表数据
-- ----------------------------
INSERT INTO `sys_user` (`user_id`, `user_name`, `nick_name`, `user_type`, `email`, `phone`, `create_by`, `create_time`)
VALUES ('1020220909000001', 'admin', 'admin01', '00', 'ytnmgg@126.com', '15888888888', 'admin', sysdate());

-- ----------------------------
-- 1. 角色信息表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id`                      bigint(16)      NOT NULL AUTO_INCREMENT,
    `role_id`                 varchar(16)     NOT NULL                   comment '角色ID',
    `role_name`               varchar(64)     NOT NULL                   comment '角色名称',
    `role_key`                varchar(64)    NOT NULL                    comment '角色权限字符串',
    `status`                  char(1)         DEFAULT '0'                comment '角色状态（0正常 1停用）',
    `del_flag`                char(1)         DEFAULT '0'                comment '删除标志（0代表存在 1代表删除）',
    `create_by`               varchar(64)     DEFAULT ''                 comment '创建者',
    `create_time`             datetime                                   comment '创建时间',
    `update_by`               varchar(64)     DEFAULT ''                 comment '更新者',
    `update_time`             datetime                                   comment '更新时间',
    `remark`                  varchar(500)    DEFAULT NULL               comment '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_id` (`role_id`),
    KEY `idx_role_key` (`role_key`)
) ENGINE=InnoDB AUTO_INCREMENT=1 comment='角色信息表';

-- ----------------------------
-- 初始化-角色信息表数据
-- ----------------------------
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `create_by`, `create_time`)
VALUES ('1120220909000001', '管理员', 'ADMIN', 'admin', sysdate());

-- ----------------------------
-- 2. 权限表
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
    `id`                bigint(16)      NOT NULL AUTO_INCREMENT,
    `permission_id`     varchar(16)     NOT NULL                   comment '权限ID',
    `permission_name`   varchar(64)     NOT NULL                   comment '权限名称',
    `permission_key`    varchar(64)     NOT NULL                   comment '权限名称',
    `status`            char(1)         DEFAULT 0                  comment '权限状态（0正常 1停用）',
    `create_by`         varchar(64)     DEFAULT ''                 comment '创建者',
    `create_time`       datetime                                   comment '创建时间',
    `update_by`         varchar(64)     DEFAULT ''                 comment '更新者',
    `update_time`       datetime                                   comment '更新时间',
    `remark`            varchar(500)    DEFAULT ''                 comment '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_id` (`permission_id`),
    KEY  `idx_permission_key` (`permission_key`)
) ENGINE=InnoDB AUTO_INCREMENT=1 comment='权限表';

-- ----------------------------
-- 初始化-权限表数据
-- ----------------------------
INSERT INTO `sys_permission` (`permission_id`, `permission_name`, `permission_key`, `create_by`, `create_time`)
VALUES ('1220220909000001', '用户查询', 'USER_QUERY', 'admin', sysdate());
INSERT INTO `sys_permission` (`permission_id`, `permission_name`, `permission_key`, `create_by`, `create_time`)
VALUES ('1220220909000002', '用户管理', 'USER_MANAGE', 'admin', sysdate());

-- ----------------------------
-- 3. 用户和角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id`                bigint(16)      NOT NULL AUTO_INCREMENT,
    `user_role_id`      varchar(16)     NOT NULL                    comment '用户-角色关系ID',
    `user_id`           varchar(16)     NOT NULL                    comment '用户ID',
    `role_id`           varchar(16)     NOT NULL                    comment '角色ID',
    `create_by`         varchar(64)     DEFAULT ''                 comment '创建者',
    `create_time`       datetime                                   comment '创建时间',
    `update_by`         varchar(64)     DEFAULT ''                 comment '更新者',
    `update_time`       datetime                                   comment '更新时间',
    `remark`            varchar(500)    DEFAULT ''                 comment '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role_id` (`user_role_id`),
    KEY  `idx_user_id` (`user_id`),
    KEY  `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 comment='用户和角色关联表';

-- ----------------------------
-- 初始化-用户和角色关联表数据
-- ----------------------------
INSERT INTO `sys_user_role` (`user_role_id`, `user_id`, `role_id`, `create_by`, `create_time`)
VALUES ('1320220909000001', '0020220909000001', '0120220909000001', 'admin', sysdate());

-- ----------------------------
-- 4. 角色和权限关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
    `id`                    bigint(16)      NOT NULL AUTO_INCREMENT,
    `role_permission_id`    varchar(16)     NOT NULL                   comment '角色-权限关系ID',
    `role_id`               varchar(16)     NOT NULL                   comment '角色ID',
    `permission_id`         varchar(16)     NOT NULL                   comment '权限ID',
    `create_by`             varchar(64)     DEFAULT ''                 comment '创建者',
    `create_time`           datetime                                   comment '创建时间',
    `update_by`             varchar(64)     DEFAULT ''                 comment '更新者',
    `update_time`           datetime                                   comment '更新时间',
    `remark`                varchar(500)    DEFAULT ''                 comment '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission_id` (`role_permission_id`),
    KEY  `idx_permission_id` (`permission_id`),
    KEY  `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 comment='角色和权限关联表';

-- ----------------------------
-- 初始化-用户和角色关联表数据
-- ----------------------------
INSERT INTO `sys_role_permission` (`role_permission_id`, `role_id`, `permission_id`, `create_by`, `create_time`)
VALUES ('1420220909000001', '0120220909000001', '0220220909000001', 'admin', sysdate());
INSERT INTO `sys_role_permission` (`role_permission_id`, `role_id`, `permission_id`, `create_by`, `create_time`)
VALUES ('1420220909000002', '0120220909000001', '0220220909000002', 'admin', sysdate());

-- ----------------------------
-- 5. sequence表
-- ----------------------------
DROP TABLE IF EXISTS `sys_sequence`;
CREATE TABLE `sys_sequence` (
  `id`              bigint(16)      NOT NULL AUTO_INCREMENT,
  `name`            varchar(32)     NOT NULL                    comment 'sequence名称',
  `current_value`   bigint(32)      NOT NULL                    comment '当前值（已被占用的最大值，新值只能比该值大）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 comment='sequence表';

-- ----------------------------
-- 6. 操作日志记录
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id`              bigint(16)      NOT NULL AUTO_INCREMENT,
  `log_id`          varchar(16)     NOT NULL                    comment '日志id',
  `method`          varchar(64)     DEFAULT ''                  comment '方法名称',
  `user_id`         varchar(16)     NOT NULL                    comment '用户ID',
  `create_time`     datetime                                    comment '创建时间',
  `remark`          varchar(2000)   DEFAULT ''                  comment '操作内容',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_log_id` (`log_id`),
  KEY  `idx_user_id` (`user_id`),
  KEY  `idx_method` (`method`)
) ENGINE=InnoDB AUTO_INCREMENT=1 comment='操作日志记录';
