CREATE TABLE IF NOT EXISTS api
(
  name           VARCHAR(128)    DEFAULT NULL COMMENT '名称',
  doc            VARCHAR(128)    DEFAULT NULL COMMENT '描述',
  permissions_id BIGINT UNSIGNED DEFAULT 3 COMMENT '访问需要权限',
  api_path       TEXT COMMENT '路径',
  api_method     VARCHAR(1023) COMMENT '请求方式',
  api_protocol   VARCHAR(63)     DEFAULT NULL COMMENT '请求协议',
  INDEX (permissions_id) COMMENT '外联 权限'
) DEFAULT CHARSET = utf8mb4,
  COMMENT 'api';
CALL add_base_struct('api');


CREATE TABLE IF NOT EXISTS api_call_record
(
  api_id          BIGINT UNSIGNED NOT NULL COMMENT 'api',
  device_code     TEXT            NULL COMMENT '设备 id, 浏览器为 agent',
  req_ip          VARCHAR(63)     NULL COMMENT '请求 ip',
  login_ip        VARCHAR(63)     NULL COMMENT '登录 ip',
  resp_code       INT             NULL COMMENT '响应码',
  resp_result_enc TEXT COMMENT '请求结果',
  INDEX (api_id) COMMENT '外联 api'
) DEFAULT CHARSET = utf8mb4, COMMENT 'API请求记录';
CALL add_base_struct('api_call_record');


CREATE TABLE IF NOT EXISTS attachment
(
  meta_name VARCHAR(127)    NULL COMMENT '原始名称',
  save_name VARCHAR(127) COMMENT '存储后名称',
  base_url  VARCHAR(255)    NULL COMMENT '基本url',
  url_name  VARCHAR(127)    NULL COMMENT '资源路径名称',
  url_doc   VARCHAR(255) COMMENT '资源路径描述',
  url_id    BIGINT UNSIGNED NULL COMMENT '根路径链接路径自连接id',
  att_type  INT             NOT NULL COMMENT '附件类别（URL、附件）',
  size      BIGINT UNSIGNED DEFAULT NULL COMMENT '文件大小',
  mime_type VARCHAR(63) COMMENT 'MIME TYPE',
  INDEX (url_id) COMMENT '自连接 id',
  INDEX (meta_name) COMMENT '原始名称经常搜索',
  INDEX (base_url) COMMENT '根路径经常检索',
  INDEX (att_type) COMMENT '附件类型经常检索',
  INDEX (mime_type) COMMENT '媒体类型经常检索'
) DEFAULT CHARSET = utf8mb4, COMMENT '文件';
CALL add_base_struct('attachment');


CREATE TABLE IF NOT EXISTS address
(
  code   VARCHAR(255) COMMENT '代码',
  name   VARCHAR(127) COMMENT '名称', # 中国最长的地名是新疆维吾尔自治区昌吉回族自治州木垒哈萨克自治县大南沟乌孜别克族乡
  level  INT     DEFAULT 0 COMMENT '级别 0 为国家',
  center VARCHAR(255) NULL COMMENT '定位',
  leaf   BOOLEAN DEFAULT FALSE COMMENT '是否为终结地址（如市辖区）',
  UNIQUE (code) COMMENT '行政区代码唯一',
  INDEX (name) COMMENT '名称经常检索'
) DEFAULT CHARSET = utf8mb4,COMMENT '行政区代码';
CALL add_base_struct('address');
CALL add_presort_tree_struct('address');

# 添加地址初始节点
INSERT INTO address
SET id=0,
    level=0,
    code=NULL,
    name='',
    rln=1,
    rrn=2,
    tgi=0,
    center=NULL;


CREATE TABLE IF NOT EXISTS address_details
(
  address_id      BIGINT UNSIGNED NOT NULL COMMENT '地址id',
  user_id         BIGINT UNSIGNED NULL COMMENT '用户 id',
  phone           VARCHAR(127) COMMENT '联系电话',
  name            VARCHAR(255) COMMENT '联系人名称',
  address_code    VARCHAR(31)     NOT NULL COMMENT '地址代码',
  address_details VARCHAR(255)    NOT NULL COMMENT '地址详情',
  center          VARCHAR(255) COMMENT '定位',
  INDEX (address_id) COMMENT '外联 地址',
  INDEX (user_id) COMMENT '外联 用户',
  INDEX (address_code) COMMENT '外联 地址代码'
) DEFAULT CHARSET = utf8mb4,COMMENT '地址详情';
CALL add_base_struct('address_details');


CREATE TABLE IF NOT EXISTS table_row_delete_record
(
  table_names     VARCHAR(127)    NULL COMMENT '表名',
  user_id         BIGINT UNSIGNED NULL COMMENT '删除用户id',
  user_account    VARCHAR(255)    NULL COMMENT '删除用户账户',
  delete_datetime DATETIME DEFAULT NOW() COMMENT '删除时间',
  entity          JSON            NOT NULL COMMENT '删除实体',
  INDEX (table_names) COMMENT '表名经常查询',
  INDEX (user_account) COMMENT '用户账户经常查询',
  INDEX (user_id) COMMENT '外联 用户'
) DEFAULT CHARSET = utf8mb4,COMMENT '数据删除记录';
CALL add_base_struct('table_row_delete_record');


CREATE TABLE IF NOT EXISTS table_row_change_record
(
  type                     BOOLEAN         NOT NULL COMMENT '变更类型：插入：true，修改：false',
  table_names              VARCHAR(127)    NULL COMMENT '表名',
  create_user_id           BIGINT UNSIGNED NULL COMMENT '创建用户id',
  create_user_account      CHAR(255)       NULL COMMENT '创建用户账户',
  create_datetime          DATETIME        NULL COMMENT '创建时间',
  create_entity            JSON            NULL COMMENT '创建实体',
  last_modify_user_id      BIGINT UNSIGNED NULL COMMENT '最后修改用户id',
  last_modify_user_account CHAR(255)       NULL COMMENT '最后修改用户账户',
  last_modify_datetime     DATETIME        NULL COMMENT '最后修改时间',
  last_modify_entity       JSON            NOT NULL COMMENT '最后修改实体',
  INDEX (table_names) COMMENT '表名经常查询',
  INDEX (create_user_account) COMMENT '创建账户经常查询',
  INDEX (last_modify_user_account) COMMENT '最后修改用户账户经常查询',
  INDEX (create_user_id) COMMENT '外联 用户',
  INDEX (last_modify_user_id) COMMENT '外联 用户'
) DEFAULT CHARSET = utf8mb4,COMMENT '数据变更记录';
CALL add_base_struct('table_row_change_record');
