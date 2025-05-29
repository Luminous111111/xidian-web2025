DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
                        `no` varchar(20) DEFAULT NULL COMMENT '账号',
                        `name` varchar(100) NOT NULL COMMENT '名字',
                        `password` varchar(20) NOT NULL COMMENT '密码',
                        `age` int(11) DEFAULT NULL,
                        `sex` int(11) DEFAULT NULL COMMENT '性别',
                        `phone` varchar(20) DEFAULT NULL COMMENT '电话',
                        `role_id` int(11) DEFAULT NULL COMMENT '角色 0系统管理员，1主管，2员工',
                        `isValid` varchar(4) DEFAULT 'Y' COMMENT '是否有效，Y有效，其他无效',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (1, 'sa', '系统管理员', '123', 18, 1, '111', 0, 'Y');
COMMIT;

CREATE TABLE `menu` (
                        `id` int(11) NOT NULL,
                        `menuCode` varchar(8) DEFAULT NULL COMMENT '菜单编码',
                        `menuName` varchar(16) DEFAULT NULL COMMENT '菜单名字',
                        `menuLevel` varchar(2) DEFAULT NULL COMMENT '菜单级别',
                        `menuParentCode` varchar(8) DEFAULT NULL COMMENT '菜单的父code',
                        `menuClick` varchar(16) DEFAULT NULL COMMENT '点击触发的函数',
                        `menuRight` varchar(8) DEFAULT NULL COMMENT '权限 0系统管理员，1表示主管，2表示员工，可以用逗号组合使用',
                        `menuComponent` varchar(200) DEFAULT NULL,
                        `menuIcon` varchar(100) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


BEGIN;
INSERT INTO `menu` VALUES (1, '001', '主管管理', '1', NULL, 'Admin', '0', 'admin/AdminManage.vue', 'el-icon-s-custom');
INSERT INTO `menu` VALUES (2, '002', '员工管理', '1', NULL, 'User', '0,1', 'user/UserManage.vue', 'el-icon-user-solid');
INSERT INTO `menu` VALUES (3, '003', '任务管理', '1', NULL, 'Task', '0,1', 'task/TaskManage.vue', 'el-icon-office-building');
-- 下面的还没做
INSERT INTO `menu` VALUES (4, '004', '任务进度管理', '1', NULL, 'Schedule', '2', 'schedule/ScheduleManage.vue', 'el-icon-menu');
INSERT INTO `menu` VALUES (5, '005', '记录管理', '1', NULL, 'Record', '0,1,2', 'record/RecordManage', 'el-icon-s-order');
COMMIT;




-- DROP TABLE IF EXISTS `record`;
-- CREATE TABLE `record` (
--                           `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
--                           `goods` int(11) NOT NULL COMMENT '货品id',
--                           `userId` int(11) DEFAULT NULL COMMENT '取货人/补货人',
--                           `admin_id` int(11) DEFAULT NULL COMMENT '操作人id',
--                           `count` int(11) DEFAULT NULL COMMENT '数量',
--                           `createtime` datetime DEFAULT NULL COMMENT '操作时间',
--                           `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
--                           PRIMARY KEY (`id`)
-- ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- 任务表
drop table if exists `task`;
create table `task` (
                        `id` bigint not null auto_increment primary key comment '主键',
                        `title` varchar(100) not null comment '任务标题',
                        `description` text comment '任务描述',
                        `status` int not null default 0 comment '状态 0：未开始，1：分析设计，2：基本搭建，3：项目开发，4：测试，5：完成',
                        `creator_id` bigint not null comment '创建人（主管）',
                        `assignee_id` bigint not null comment '被分配人（员工）',
                        `create_time` datetime not null default current_timestamp comment '创建时间',
                        `update_time` datetime not null default current_timestamp on update current_timestamp comment '更新时间',
                        `deadline` datetime comment '截止时间',
                        `assigneeName` varchar(100) not null comment '被分配人名字',
                        key `idx_assignee_id` (`assignee_id`),
                        key `idx_creator_id` (`creator_id`)
) engine=innodb default charset=utf8mb4 comment='任务表';


-- 任务评论表
drop table if exists `task_comment`;
create table `task_comment` (
                                `id` bigint not null auto_increment primary key comment '主键',
                                `task_id` bigint not null comment '任务ID',
                                `user_id` bigint not null comment '评论人',
                                `content` text not null comment '评论内容',
                                `create_time` datetime not null default current_timestamp comment '评论时间',
                                key `idx_task_id` (`task_id`),
                                key `idx_user_id` (`user_id`)
) engine=innodb default charset=utf8mb4 comment='任务评论表';

-- ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '123456';
-- FLUSH PRIVILE;