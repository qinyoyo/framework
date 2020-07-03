-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details` (
  `client_id` varchar(255) NOT NULL PRIMARY KEY COMMENT 'client_id',
  `client_secret` varchar(255) NOT NULL COMMENT 'client_secret',
  `scope` varchar(255) NOT NULL COMMENT 'scope',
  `authorized_grant_types` varchar(255) NOT NULL COMMENT '认证类型',
  `web_server_redirect_uri` varchar(255) NULL COMMENT 'redirect_url',
  `authorities` varchar(255) NOT NULL COMMENT '权限集',
  `access_token_validity` int(11) DEFAULT 86400 NOT NULL COMMENT 'token有效时间',
  `refresh_token_validity` int(11) DEFAULT 0 NOT NULL COMMENT '刷新时间秒',
  `autoapprove` varchar(20) DEFAULT '1' NOT NULL COMMENT '自动授权',
  `expired_date` datetime NULL COMMENT '过期时间',
  `locked` tinyint(1) DEFAULT 0 NOT NULL COMMENT '锁定',
  `enabled` tinyint(1) DEFAULT 1 NOT NULL COMMENT '激活',
  `decription` varchar(200) NULL COMMENT '描述',
  `additional_information` text NULL COMMENT '附加属性(json)',
  `resource_ids` varchar(255) NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'OAuth2客户端配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_action
-- ----------------------------
DROP TABLE IF EXISTS `sys_action`;
CREATE TABLE `sys_action` (
  `note` varchar(200) NULL COMMENT '描述',
  `id` int(11) AUTO_INCREMENT NOT NULL PRIMARY KEY COMMENT 'ID',
  `value` varchar(100) NOT NULL COMMENT '值',
  `params` varchar(100) NULL COMMENT '参数',
  `method` varchar(45) NULL COMMENT '方法'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '功能列表' ROW_FORMAT = Dynamic;
CREATE INDEX `index_sys_action_value` ON `sys_action`(`value`);

-- ----------------------------
-- Table structure for sys_data_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_data_dict`;
CREATE TABLE `sys_data_dict` (
  `id` int(11) AUTO_INCREMENT NOT NULL PRIMARY KEY,
  `dict` varchar(45) NOT NULL COMMENT '分类',
  `code` varchar(45) NOT NULL COMMENT '代码',
  `text` varchar(100) NOT NULL COMMENT '显示值',
  `order_num` int(11) NULL COMMENT '排列顺序'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '数据字典' ROW_FORMAT = Dynamic;

ALTER TABLE `sys_data_dict` ADD CONSTRAINT  `ui_sys_data_dict01` UNIQUE (`dict`,`code`);
-- ----------------------------
-- Table structure for sys_flow_process
-- ----------------------------
DROP TABLE IF EXISTS `sys_flow_process`;
CREATE TABLE `sys_flow_process` (
  `id` char(32) NOT NULL PRIMARY KEY,
  `flow_id` varchar(36) NOT NULL COMMENT '流程单',
  `operation` varchar(36) NULL COMMENT '操作',
  `selection` varchar(36) NULL COMMENT '选择',
  `state_name0` varchar(45) NULL COMMENT '初始状态',
  `state_name1` varchar(45) NULL COMMENT '处理后状态',
  `operator` varchar(36) NOT NULL COMMENT '处理人',
  `time` datetime NOT NULL COMMENT '处理时间',
  `note` varchar(200) NULL COMMENT '处理意见',
  `next_operator` varchar(45) NULL COMMENT '后续处理人',
  `state0` int(11) NULL COMMENT '初始状态',
  `state1` int(11) NOT NULL COMMENT '处理后状态'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '流程处理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` char(32) NOT NULL PRIMARY KEY COMMENT 'uuid主键',
  `time` datetime NOT NULL COMMENT '时间',
  `keyword` varchar(100) NOT NULL COMMENT '关键词',
  `user_type` varchar(64) NULL COMMENT '用户类别',
  `user_info` varchar(64) NULL COMMENT '用户信息',
  `note` varchar(1000) NULL COMMENT '详情'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` int(11) AUTO_INCREMENT NOT NULL PRIMARY KEY,
  `sort_num` int(11) DEFAULT 10 NOT NULL COMMENT '排列顺序',
  `name` varchar(45) NOT NULL COMMENT '名称',
  `parent` int(11) NULL COMMENT '上级菜单',
  `is_divider` tinyint(1) DEFAULT 0 NOT NULL COMMENT '分割线',
  `icon` varchar(50) NULL COMMENT '图标',
  `action` varchar(100) NULL COMMENT '功能地址',
  `class_name` varchar(45) NULL COMMENT '附加属性',
  `data_action` varchar(200) NULL COMMENT '附加参数',
  `bigIcon` varchar(50) NULL COMMENT '大图标',
  `mobile_hidden` tinyint(1) DEFAULT 0 NOT NULL COMMENT '手机隐藏',
  `js_cmd` varchar(45) NULL COMMENT '操作函数'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统菜单' ROW_FORMAT = Dynamic;

ALTER TABLE `sys_menu` ADD CONSTRAINT  `ui_sys_menu012` UNIQUE (`name`,`parent`,`action`);
-- ----------------------------
-- Table structure for sys_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message` (
  `id` int(11) AUTO_INCREMENT NOT NULL PRIMARY KEY COMMENT '主键',
  `read_time` datetime NULL COMMENT '已阅',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `receiver_name` varchar(50) NULL COMMENT '消息接收人姓名',
  `sender_name` varchar(50) NULL COMMENT '消息发送人姓名',
  `title` varchar(100) NOT NULL COMMENT '消息标题',
  `content` text NOT NULL COMMENT '消息内容',
  `url` varchar(100) NULL COMMENT '消息点击链接',
  `receiver` varchar(50) NULL COMMENT '消息接收人',
  `sender` varchar(100) NULL COMMENT '消息发送人',
  `event_id` varchar(45) NULL COMMENT '关联id'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '消息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_org
-- ----------------------------
DROP TABLE IF EXISTS `sys_org`;
CREATE TABLE `sys_org` (
  `org_code` int(11) AUTO_INCREMENT NOT NULL PRIMARY KEY COMMENT '代码',
  `name` varchar(32) NOT NULL COMMENT '名称',
  `parent` int(11) NULL COMMENT '上级机构',
  `phone` varchar(32) NULL COMMENT '电话',
  `fax` varchar(32) NULL COMMENT '传真',
  `role_list` varchar(200) NULL COMMENT '角色序列',
  `address` varchar(100) NULL COMMENT '地址'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '组织机构' ROW_FORMAT = Dynamic;

ALTER TABLE `sys_org` ADD CONSTRAINT  `ui_sys_org01` UNIQUE (`name`,`parent`);
-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` int(11) AUTO_INCREMENT NOT NULL PRIMARY KEY,
  `role` int(11) NOT NULL COMMENT '角色',
  `action` int(11) NOT NULL COMMENT '功能'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '功能分配表' ROW_FORMAT = Dynamic;
CREATE INDEX `index_sys_permission_role` ON `sys_permission`(`role`);
CREATE INDEX `index_sys_permission_action` ON `sys_permission`(`action`);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` int(11) AUTO_INCREMENT NOT NULL PRIMARY KEY,
  `code` varchar(8) NOT NULL UNIQUE COMMENT '代码',
  `name` varchar(45) NOT NULL UNIQUE COMMENT '名称',
  `note` varchar(64) NULL COMMENT '说明'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_route
-- ----------------------------
DROP TABLE IF EXISTS `sys_route`;
CREATE TABLE `sys_route` (
  `id` int(11) AUTO_INCREMENT NOT NULL PRIMARY KEY COMMENT 'ID',
  `path` varchar(200) NOT NULL UNIQUE COMMENT '路径',
  `name` varchar(100) NOT NULL UNIQUE COMMENT '名称',
  `component` varchar(200) DEFAULT 'Layout' NOT NULL COMMENT '组件',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `breadcrumb_show` tinyint(1) DEFAULT 1 NOT NULL COMMENT '面包屑显示',
  `need_cache` tinyint(1) DEFAULT 1 NOT NULL COMMENT '需要缓存',
  `tag_view` tinyint(1) DEFAULT 1 NOT NULL COMMENT '标签显示'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '路由表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_task
-- ----------------------------
DROP TABLE IF EXISTS `sys_task`;
CREATE TABLE `sys_task` (
  `id` int(11) AUTO_INCREMENT NOT NULL PRIMARY KEY,
  `name` varchar(45) NOT NULL COMMENT '任务名',
  `description` varchar(300) NULL COMMENT '详细说明',
  `scedule_years` varchar(100) DEFAULT '*' NOT NULL COMMENT '调度年',
  `schedule_months` varchar(100) DEFAULT '*' NOT NULL COMMENT '调度月',
  `schedule_days` varchar(100) DEFAULT '*' NOT NULL COMMENT '调度日',
  `schedule_weeks` varchar(100) DEFAULT '*' NOT NULL COMMENT '调度星期',
  `schedule_hours` varchar(100) DEFAULT '*' NOT NULL COMMENT '调度时',
  `schedule_minutes` varchar(100) DEFAULT '*' NOT NULL COMMENT '调度分',
  `class_name` varchar(100) NOT NULL COMMENT '类名',
  `mothod_name` varchar(500) NOT NULL COMMENT '方法',
  `is_static` tinyint(1) DEFAULT 1 NOT NULL COMMENT '静态方法',
  `parames` varchar(100) NULL COMMENT '参数',
  `is_running` tinyint(1) DEFAULT 1 NOT NULL COMMENT '运行中'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '调度任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` varchar(32) NOT NULL PRIMARY KEY COMMENT 'uuid主键',
  `icon` text NULL COMMENT '头像',
  `name` varchar(32) NOT NULL UNIQUE COMMENT '用户名',
  `real_name` varchar(32) NOT NULL COMMENT '真实名',
  `sex` varchar(1) DEFAULT '1' NULL COMMENT '性别',
  `birth_date` date NULL COMMENT '生日',
  `reg_time` datetime NULL COMMENT '注册时间',
  `mobile_phone` varchar(32) NULL UNIQUE COMMENT '手机号码',
  `email` varchar(50) NULL UNIQUE COMMENT '邮箱',
  `wechat_openid` varchar(50) NULL UNIQUE COMMENT '微信openId',
  `org_code` int(11) NOT NULL COMMENT '部门',
  `employee_no` varchar(32) NULL UNIQUE COMMENT '工号',
  `org_privilege` int(11) NULL COMMENT '部门权限',
  `role_list` varchar(200) NULL COMMENT '角色序列',
  `locked` tinyint(1) DEFAULT 0 NOT NULL COMMENT '被锁定',
  `state` tinyint(1) DEFAULT 1 NOT NULL COMMENT '激活',
  `id_type` int(11) DEFAULT 1 NOT NULL COMMENT '证件类型',
  `id_number` varchar(45) NOT NULL COMMENT '证件号码',
  `expired_time` date NULL COMMENT '账号过期时间',
  `password_expired_time` date NULL COMMENT '密码过期时间',
  `password` varchar(32) DEFAULT '123456' NOT NULL COMMENT '密码',
  `password_error_times` int(11) DEFAULT 0 NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户' ROW_FORMAT = Dynamic;
CREATE INDEX `index_sys_user_org_code` ON `sys_user`(`org_code`);

ALTER TABLE `sys_user` ADD CONSTRAINT  `ui_sys_user01` UNIQUE (`id_type`,`id_number`);
