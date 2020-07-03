-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[oauth_client_details]') AND type IN ('U'))
	DROP TABLE [dbo].[oauth_client_details]
GO
CREATE TABLE [dbo].[oauth_client_details] (
  [client_id] varchar(255) NOT NULL PRIMARY KEY,
  [client_secret] varchar(255) NOT NULL,
  [scope] varchar(255) NOT NULL,
  [authorized_grant_types] varchar(255) NOT NULL,
  [web_server_redirect_uri] varchar(255) NULL,
  [authorities] varchar(255) NOT NULL,
  [access_token_validity] int DEFAULT ((86400)) NOT NULL,
  [refresh_token_validity] int DEFAULT ((0)) NOT NULL,
  [autoapprove] varchar(20) DEFAULT ('1') NOT NULL,
  [expired_date] datetime NULL,
  [locked] bit DEFAULT ((0)) NOT NULL,
  [enabled] bit DEFAULT ((1)) NOT NULL,
  [decription] varchar(200) NULL,
  [additional_information] text NULL,
  [resource_ids] varchar(255) NULL
)
GO
ALTER TABLE [dbo].[oauth_client_details] SET (LOCK_ESCALATION = TABLE)
GO

-- ----------------------------
-- Table structure for sys_action
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[sys_action]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_action]
GO
CREATE TABLE [dbo].[sys_action] (
  [note] varchar(200) NULL,
  [id] int IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [value] varchar(100) NOT NULL,
  [params] varchar(100) NULL,
  [method] varchar(45) NULL
)
GO
ALTER TABLE [dbo].[sys_action] SET (LOCK_ESCALATION = TABLE)
GO
CREATE INDEX [index_sys_action_value] ON [dbo].[sys_action]([value]);

-- ----------------------------
-- Table structure for sys_data_dict
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[sys_data_dict]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_data_dict]
GO
CREATE TABLE [dbo].[sys_data_dict] (
  [id] int IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [dict] varchar(45) NOT NULL,
  [code] varchar(45) NOT NULL,
  [text] varchar(100) NOT NULL,
  [order_num] int NULL
)
GO
ALTER TABLE [dbo].[sys_data_dict] SET (LOCK_ESCALATION = TABLE)
GO

ALTER TABLE [dbo].[sys_data_dict] ADD CONSTRAINT  [ui_sys_data_dict01] UNIQUE ([dict],[code])
GO
-- ----------------------------
-- Table structure for sys_flow_process
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[sys_flow_process]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_flow_process]
GO
CREATE TABLE [dbo].[sys_flow_process] (
  [id] char(32) NOT NULL PRIMARY KEY,
  [flow_id] varchar(36) NOT NULL,
  [operation] varchar(36) NULL,
  [selection] varchar(36) NULL,
  [state_name0] varchar(45) NULL,
  [state_name1] varchar(45) NULL,
  [operator] varchar(36) NOT NULL,
  [time] datetime NOT NULL,
  [note] varchar(200) NULL,
  [next_operator] varchar(45) NULL,
  [state0] int NULL,
  [state1] int NOT NULL
)
GO
ALTER TABLE [dbo].[sys_flow_process] SET (LOCK_ESCALATION = TABLE)
GO

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[sys_log]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_log]
GO
CREATE TABLE [dbo].[sys_log] (
  [id] char(32) NOT NULL PRIMARY KEY,
  [time] datetime NOT NULL,
  [keyword] varchar(100) NOT NULL,
  [user_type] varchar(64) NULL,
  [user_info] varchar(64) NULL,
  [note] varchar(1000) NULL
)
GO
ALTER TABLE [dbo].[sys_log] SET (LOCK_ESCALATION = TABLE)
GO

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[sys_menu]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_menu]
GO
CREATE TABLE [dbo].[sys_menu] (
  [id] int IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [sort_num] int DEFAULT ((10)) NOT NULL,
  [name] varchar(45) NOT NULL,
  [parent] int NULL,
  [is_divider] bit DEFAULT ((0)) NOT NULL,
  [icon] varchar(50) NULL,
  [action] varchar(100) NULL,
  [class_name] varchar(45) NULL,
  [data_action] varchar(200) NULL,
  [bigIcon] varchar(50) NULL,
  [mobile_hidden] bit DEFAULT ((0)) NOT NULL,
  [js_cmd] varchar(45) NULL
)
GO
ALTER TABLE [dbo].[sys_menu] SET (LOCK_ESCALATION = TABLE)
GO

ALTER TABLE [dbo].[sys_menu] ADD CONSTRAINT  [ui_sys_menu012] UNIQUE ([name],[parent],[action])
GO
-- ----------------------------
-- Table structure for sys_message
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[sys_message]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_message]
GO
CREATE TABLE [dbo].[sys_message] (
  [id] int IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [read_time] datetime NULL,
  [create_time] datetime NOT NULL,
  [receiver_name] varchar(50) NULL,
  [sender_name] varchar(50) NULL,
  [title] varchar(100) NOT NULL,
  [content] text NOT NULL,
  [url] varchar(100) NULL,
  [receiver] varchar(50) NULL,
  [sender] varchar(100) NULL,
  [event_id] varchar(45) NULL
)
GO
ALTER TABLE [dbo].[sys_message] SET (LOCK_ESCALATION = TABLE)
GO

-- ----------------------------
-- Table structure for sys_org
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[sys_org]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_org]
GO
CREATE TABLE [dbo].[sys_org] (
  [org_code] int IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [name] varchar(32) NOT NULL,
  [parent] int NULL,
  [phone] varchar(32) NULL,
  [fax] varchar(32) NULL,
  [role_list] varchar(200) NULL,
  [address] varchar(100) NULL
)
GO
ALTER TABLE [dbo].[sys_org] SET (LOCK_ESCALATION = TABLE)
GO

ALTER TABLE [dbo].[sys_org] ADD CONSTRAINT  [ui_sys_org01] UNIQUE ([name],[parent])
GO
-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[sys_permission]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_permission]
GO
CREATE TABLE [dbo].[sys_permission] (
  [id] int IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [role] int NOT NULL,
  [action] int NOT NULL
)
GO
ALTER TABLE [dbo].[sys_permission] SET (LOCK_ESCALATION = TABLE)
GO
CREATE INDEX [index_sys_permission_role] ON [dbo].[sys_permission]([role]);
CREATE INDEX [index_sys_permission_action] ON [dbo].[sys_permission]([action]);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[sys_role]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_role]
GO
CREATE TABLE [dbo].[sys_role] (
  [id] int IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [code] varchar(8) NOT NULL UNIQUE,
  [name] varchar(45) NOT NULL UNIQUE,
  [note] varchar(64) NULL
)
GO
ALTER TABLE [dbo].[sys_role] SET (LOCK_ESCALATION = TABLE)
GO

-- ----------------------------
-- Table structure for sys_route
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[sys_route]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_route]
GO
CREATE TABLE [dbo].[sys_route] (
  [id] int IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [path] varchar(200) NOT NULL UNIQUE,
  [name] varchar(100) NOT NULL UNIQUE,
  [component] varchar(200) DEFAULT ('Layout') NOT NULL,
  [title] varchar(100) NOT NULL,
  [breadcrumb_show] bit DEFAULT ((1)) NOT NULL,
  [need_cache] bit DEFAULT ((1)) NOT NULL,
  [tag_view] bit DEFAULT ((1)) NOT NULL
)
GO
ALTER TABLE [dbo].[sys_route] SET (LOCK_ESCALATION = TABLE)
GO

-- ----------------------------
-- Table structure for sys_task
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[sys_task]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_task]
GO
CREATE TABLE [dbo].[sys_task] (
  [id] int IDENTITY(1,1) NOT NULL PRIMARY KEY,
  [name] varchar(45) NOT NULL,
  [description] varchar(300) NULL,
  [scedule_years] varchar(100) DEFAULT ('*') NOT NULL,
  [schedule_months] varchar(100) DEFAULT ('*') NOT NULL,
  [schedule_days] varchar(100) DEFAULT ('*') NOT NULL,
  [schedule_weeks] varchar(100) DEFAULT ('*') NOT NULL,
  [schedule_hours] varchar(100) DEFAULT ('*') NOT NULL,
  [schedule_minutes] varchar(100) DEFAULT ('*') NOT NULL,
  [class_name] varchar(100) NOT NULL,
  [mothod_name] varchar(500) NOT NULL,
  [is_static] bit DEFAULT ((1)) NOT NULL,
  [parames] varchar(100) NULL,
  [is_running] bit DEFAULT ((1)) NOT NULL
)
GO
ALTER TABLE [dbo].[sys_task] SET (LOCK_ESCALATION = TABLE)
GO

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[sys_user]') AND type IN ('U'))
	DROP TABLE [dbo].[sys_user]
GO
CREATE TABLE [dbo].[sys_user] (
  [id] varchar(32) NOT NULL PRIMARY KEY,
  [icon] text NULL,
  [name] varchar(32) NOT NULL UNIQUE,
  [real_name] varchar(32) NOT NULL,
  [sex] varchar(1) DEFAULT ('1') NULL,
  [birth_date] datetime NULL,
  [reg_time] datetime NULL,
  [mobile_phone] varchar(32) NULL,
  [email] varchar(50) NULL,
  [wechat_openid] varchar(50) NULL,
  [org_code] int NOT NULL,
  [employee_no] varchar(32) NULL,
  [org_privilege] int NULL,
  [role_list] varchar(200) NULL,
  [locked] bit DEFAULT ((0)) NOT NULL,
  [state] bit DEFAULT ((1)) NOT NULL,
  [id_type] int DEFAULT ((1)) NOT NULL,
  [id_number] varchar(45) NOT NULL,
  [expired_time] datetime NULL,
  [password_expired_time] datetime NULL,
  [password] varchar(32) DEFAULT ('123456') NOT NULL,
  [password_error_times] int DEFAULT ((0)) NOT NULL
)
GO
ALTER TABLE [dbo].[sys_user] SET (LOCK_ESCALATION = TABLE)
GO
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[fn_multi_null_sys_user_mobile_phone]') AND type IN ('FN'))
	DROP FUNCTION [dbo].[fn_multi_null_sys_user_mobile_phone]
GO
CREATE FUNCTION [dbo].[fn_multi_null_sys_user_mobile_phone](    @value varchar(32))
RETURNS bit
AS
BEGIN
  DECLARE @result bit
	DECLARE @ct  int
	IF @value is null  or @value =''
		SET @result=1
	ELSE
	  BEGIN
			select @ct=count(*) from sys_user where employee_no=@value
			select @result = case when @ct>1 then 0 else 1 end
		END	
  RETURN @result
END
GO
Alter table [dbo].[sys_user] Add constraint unique_multi_null_sys_user_mobile_phone check ([dbo].[fn_multi_null_sys_user_mobile_phone](mobile_phone)=1);
GO
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[fn_multi_null_sys_user_email]') AND type IN ('FN'))
	DROP FUNCTION [dbo].[fn_multi_null_sys_user_email]
GO
CREATE FUNCTION [dbo].[fn_multi_null_sys_user_email](    @value varchar(50))
RETURNS bit
AS
BEGIN
  DECLARE @result bit
	DECLARE @ct  int
	IF @value is null  or @value =''
		SET @result=1
	ELSE
	  BEGIN
			select @ct=count(*) from sys_user where employee_no=@value
			select @result = case when @ct>1 then 0 else 1 end
		END	
  RETURN @result
END
GO
Alter table [dbo].[sys_user] Add constraint unique_multi_null_sys_user_email check ([dbo].[fn_multi_null_sys_user_email](email)=1);
GO
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[fn_multi_null_sys_user_wechat_openid]') AND type IN ('FN'))
	DROP FUNCTION [dbo].[fn_multi_null_sys_user_wechat_openid]
GO
CREATE FUNCTION [dbo].[fn_multi_null_sys_user_wechat_openid](    @value varchar(50))
RETURNS bit
AS
BEGIN
  DECLARE @result bit
	DECLARE @ct  int
	IF @value is null  or @value =''
		SET @result=1
	ELSE
	  BEGIN
			select @ct=count(*) from sys_user where employee_no=@value
			select @result = case when @ct>1 then 0 else 1 end
		END	
  RETURN @result
END
GO
Alter table [dbo].[sys_user] Add constraint unique_multi_null_sys_user_wechat_openid check ([dbo].[fn_multi_null_sys_user_wechat_openid](wechat_openid)=1);
GO
CREATE INDEX [index_sys_user_org_code] ON [dbo].[sys_user]([org_code]);
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID('[dbo].[fn_multi_null_sys_user_employee_no]') AND type IN ('FN'))
	DROP FUNCTION [dbo].[fn_multi_null_sys_user_employee_no]
GO
CREATE FUNCTION [dbo].[fn_multi_null_sys_user_employee_no](    @value varchar(32))
RETURNS bit
AS
BEGIN
  DECLARE @result bit
	DECLARE @ct  int
	IF @value is null  or @value =''
		SET @result=1
	ELSE
	  BEGIN
			select @ct=count(*) from sys_user where employee_no=@value
			select @result = case when @ct>1 then 0 else 1 end
		END	
  RETURN @result
END
GO
Alter table [dbo].[sys_user] Add constraint unique_multi_null_sys_user_employee_no check ([dbo].[fn_multi_null_sys_user_employee_no](employee_no)=1);
GO

ALTER TABLE [dbo].[sys_user] ADD CONSTRAINT  [ui_sys_user01] UNIQUE ([id_type],[id_number])
GO
