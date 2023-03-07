CREATE TABLE IF NOT EXISTS api
(
  name           VARCHAR(128)    DEFAULT NULL COMMENT '名称',
  doc            VARCHAR(128)    DEFAULT NULL COMMENT '描述',
  permissions_id BIGINT UNSIGNED DEFAULT 3 COMMENT '访问需要权限',
  api_path       TEXT COMMENT '路径',
  api_method     VARCHAR(1023) COMMENT '请求方式',
  api_protocol   CHAR(63)        DEFAULT NULL COMMENT '请求协议',
  INDEX (permissions_id) COMMENT '外联 permissions id'
) DEFAULT CHARSET = utf8mb4,
  COMMENT 'api';
CALL base_tab('api');


CREATE TABLE IF NOT EXISTS api_call_record
(
  api_id          BIGINT UNSIGNED NOT NULL COMMENT 'api',
  device_code     TEXT          DEFAULT NULL COMMENT '设备 id, 浏览器为 agent',
  req_ip          VARCHAR(1023) DEFAULT NULL COMMENT '请求 ip',
  resp_code       CHAR(63)      DEFAULT NULL COMMENT '响应码',
  resp_result_enc TEXT COMMENT '请求结果',
  INDEX (api_id) COMMENT '外联 api id'
) DEFAULT CHARSET = utf8mb4, COMMENT 'API请求记录';
CALL base_tab('api_call_record');


CREATE TABLE IF NOT EXISTS file_location
(
  url  TEXT          NOT NULL COMMENT '基本url',
  name VARCHAR(1023) NOT NULL COMMENT '资源路径名称',
  doc  VARCHAR(1023) DEFAULT '' COMMENT '资源路径描述',
  type CHAR          NOT NULL COMMENT '存储类别'
) DEFAULT CHARSET = utf8mb4, COMMENT '文件地址';
CALL base_tab('file_location');


CREATE TABLE IF NOT EXISTS file
(
  file_location_id BIGINT UNSIGNED NOT NULL COMMENT '存储base路径',
  meta_name        TEXT            NOT NULL COMMENT '原始名称',
  save_name        TEXT COMMENT '存储后名称',
  byte_size        BIGINT UNSIGNED DEFAULT 0 COMMENT '文件大小',
  descriptor       VARCHAR(255) COMMENT '文件描述符',
  mime_type        VARCHAR(1023) COMMENT 'MIME TYPE'
) DEFAULT CHARSET = utf8mb4, COMMENT '文件';
CALL base_tab('file');


CREATE TABLE IF NOT EXISTS message
(
  msg          TEXT COMMENT '消息',
  send_user_id BIGINT UNSIGNED NOT NULL COMMENT '发送方',
  INDEX (send_user_id) COMMENT '外联 user id'
) DEFAULT CHARSET = utf8mb4,
  COMMENT '消息';
CALL base_tab('message');
CALL reference_type('message', '接收消息的主题，用户，管理员，客服');


CREATE TABLE IF NOT EXISTS message_file
(
  message_id BIGINT UNSIGNED NOT NULL COMMENT 'message',
  file_id    BIGINT UNSIGNED NOT NULL COMMENT '文件'
) DEFAULT CHARSET = utf8mb4,
  COMMENT '消息 文件';
CALL base_tab('message_file');


CREATE TABLE IF NOT EXISTS address
(
  code   VARCHAR(255) COMMENT '代码',
  name   VARCHAR(2047) COMMENT '名称',
  level  INT DEFAULT 0 COMMENT '级别 0 为国家',
  center POINT NULL COMMENT '定位'
) DEFAULT CHARSET = utf8mb4,
  COMMENT '行政区代码';
CALL base_tab('address');
CALL presort_tree_tab('address');


CREATE TABLE IF NOT EXISTS address_details
(
  address_id      BIGINT UNSIGNED NOT NULL COMMENT '地址',
  address_details BIGINT UNSIGNED NOT NULL COMMENT '地址详情',
  location        POINT COMMENT '定位'
) DEFAULT CHARSET = utf8mb4,
  COMMENT '详细地址';
CALL base_tab('address_details');


CREATE TABLE IF NOT EXISTS delete_backup
(
  lang            VARCHAR(255)  DEFAULT 'java' COMMENT '编程语言',
  namespaces      TEXT COMMENT '命名空间,例如 java 的 class, csharp 的 namespace',
  del_ser_obj     JSON COMMENT '删除数据',
  del_sys_version VARCHAR(1000) DEFAULT '0' COMMENT '系统版本'
) DEFAULT CHARSET = utf8mb4,COMMENT '数据删除备份表';
CALL base_tab('delete_backup');
