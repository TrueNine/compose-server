CREATE TABLE IF NOT EXISTS user
(
  account         VARCHAR(255) NOT NULL COMMENT '账号',
  nick_name       VARCHAR(2047) COMMENT '呢称',
  doc             TEXT COMMENT '描述',
  pwd_enc         VARCHAR(2047) DEFAULT NULL COMMENT '密码',
  ban_time        DATETIME      DEFAULT NULL COMMENT '被封禁结束时间',
  last_login_time DATETIME      DEFAULT NOW() COMMENT '最后请求时间',
  UNIQUE (account) COMMENT '账号唯一'
) DEFAULT CHARSET = utf8mb4,COMMENT '用户';
CALL add_base_struct('user');

INSERT INTO user
SET id=0,
    account='root',
    nick_name='ROOT',
    pwd_enc='$2a$14$EZlcGzhoLyqi4FNrKqgiO.mZauWIkWlSOdywJB.CXY3uEkC9ratju',
    last_login_time=NOW();


CREATE TABLE IF NOT EXISTS user_info
(
  user_id            BIGINT UNSIGNED NOT NULL COMMENT '用户',
  avatar_img_id      BIGINT UNSIGNED COMMENT '用户头像',
  first_name         VARCHAR(4095) COMMENT '姓',
  last_name          VARCHAR(4095) COMMENT '名',
  email              VARCHAR(255) COMMENT '邮箱',
  birthday           DATETIME COMMENT '生日',
  address_details_id BIGINT UNSIGNED COMMENT '地址',
  phone              VARCHAR(255) COMMENT '电话号码',
  id_card            VARCHAR(255) COMMENT '身份证',
  gender             TINYINT DEFAULT 2 COMMENT ' 性别：0女，1难，2未知',
  UNIQUE (phone) COMMENT '电话唯一',
  UNIQUE (id_card) COMMENT '身份证唯一',
  INDEX (user_id) COMMENT '外联 用户',
  INDEX (address_details_id) COMMENT '外联 地址详情',
  INDEX (avatar_img_id) COMMENT '外联 文件'
) DEFAULT CHARSET = utf8mb4, COMMENT '用户信息';
CALL add_base_struct('user_info');

INSERT INTO user_info
SET id=0,
    user_id=0,
    first_name='赵',
    last_name='日天',
    email='truenine@163.com',
    birthday='1997-11-04',
    phone='186977192235',
    gender=0;


CREATE TABLE IF NOT EXISTS role
(
  name VARCHAR(255) COMMENT '角色名称',
  doc  VARCHAR(2047) COMMENT '角色描述'
) DEFAULT CHARSET = utf8mb4, COMMENT '角色';
CALL add_base_struct('role');


INSERT INTO role
SET id=0,
    name='ROOT',
    doc='默认超级管理员角色，务必不要删除';
INSERT INTO role
SET id=1,
    name='USER',
    doc='默认USER角色，务必不要删除';


CREATE TABLE IF NOT EXISTS permissions
(
  name VARCHAR(255) COMMENT '权限名',
  doc  VARCHAR(2047) COMMENT '权限描述'
) DEFAULT CHARSET = utf8mb4, COMMENT '权限';
CALL add_base_struct('permissions');


INSERT INTO permissions
SET id=0,
    name='ROOT',
    doc='默认ROOT权限，务必不要删除';
INSERT INTO permissions
SET id=1,
    name='USER',
    doc='默认USER权限，务必不要删除';


CREATE TABLE IF NOT EXISTS role_group
(
  name VARCHAR(255) COMMENT '名称',
  doc  VARCHAR(2047) COMMENT '描述'
) DEFAULT CHARSET = utf8mb4, COMMENT '角色组';
CALL add_base_struct('role_group');


INSERT INTO role_group
SET id=0,
    name='ROOT',
    doc='默认ROOT角色组，务必不要删除';
INSERT INTO role_group
SET id=1,
    name='USER',
    doc='默认USER角色组，务必不要删除';


CREATE TABLE IF NOT EXISTS user_group
(
  user_id BIGINT UNSIGNED COMMENT '创建人',
  name    VARCHAR(255) COMMENT '名称',
  doc     VARCHAR(2047) COMMENT '描述',
  INDEX (user_id) COMMENT '外联 用户'
) DEFAULT CHARSET = utf8mb4, COMMENT '用户组';
CALL add_base_struct('user_group');


INSERT INTO user_group
SET id='0',
    user_id='0',
    name='ROOT',
    doc='ROOT 用户组，务必不要删除';


CREATE TABLE IF NOT EXISTS role_permissions
(
  role_id        BIGINT UNSIGNED COMMENT '角色',
  permissions_id BIGINT UNSIGNED COMMENT '权限',
  INDEX (role_id) COMMENT '外联 角色',
  INDEX (permissions_id) COMMENT '外联 权限'
) DEFAULT CHARSET = utf8mb4, COMMENT '角色  权限';
CALL add_base_struct('role_permissions');


INSERT INTO role_permissions
SET id=0,
    role_id=0,
    permissions_id=0;
INSERT INTO role_permissions
SET id=1,
    role_id=0,
    permissions_id=1;
INSERT INTO role_permissions
SET id=2,
    role_id=1,
    permissions_id=1;


CREATE TABLE IF NOT EXISTS role_group_role
(
  role_group_id BIGINT UNSIGNED COMMENT '用户组',
  role_id       BIGINT UNSIGNED COMMENT '角色',
  INDEX (role_group_id) COMMENT '外联 角色组',
  INDEX (role_id) COMMENT '外联 角色'
) DEFAULT CHARSET = utf8mb4,COMMENT '角色组  角色';
CALL add_base_struct('role_group_role');


INSERT INTO role_group_role
SET id=0,
    role_id=0,
    role_group_id=0;
INSERT INTO role_group_role
SET id=1,
    role_id=0,
    role_group_id=1;
INSERT INTO role_group_role
SET id=2,
    role_id=1,
    role_group_id=1;


CREATE TABLE IF NOT EXISTS user_role_group
(
  user_id       BIGINT UNSIGNED COMMENT '用户',
  role_group_id BIGINT UNSIGNED COMMENT '权限组',
  INDEX (user_id) COMMENT '外联 用户',
  INDEX (role_group_id) COMMENT '外联 角色组'
) DEFAULT CHARSET = utf8mb4, COMMENT '用户  角色组';
CALL add_base_struct('user_role_group');


INSERT INTO user_role_group
SET id=0,
    user_id=0,
    role_group_id=0;


CREATE TABLE IF NOT EXISTS user_group_role_group
(
  role_group_id BIGINT UNSIGNED COMMENT '角色组',
  user_group_id BIGINT UNSIGNED COMMENT '用户组',
  INDEX (role_group_id) COMMENT '外联 角色组',
  INDEX (user_group_id) COMMENT '外联 用户组'
) DEFAULT CHARSET = utf8mb4, COMMENT '用户组  角色组';
CALL add_base_struct('user_group_role_group');


CREATE TABLE IF NOT EXISTS user_group_user
(
  user_group_id BIGINT UNSIGNED NOT NULL COMMENT '用户组',
  user_id       BIGINT UNSIGNED NOT NULL COMMENT '用户',
  INDEX (user_group_id) COMMENT '外联 用户组',
  INDEX (user_id) COMMENT '外联 用户'
) DEFAULT CHARSET = utf8mb4,COMMENT '用户组 用户';
CALL add_base_struct('user_group_user');
