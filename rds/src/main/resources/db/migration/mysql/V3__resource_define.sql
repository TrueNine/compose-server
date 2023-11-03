create table if not exists api
(
  name           varchar(128)    default null comment '名称',
  doc            varchar(128)    default null comment '描述',
  permissions_id bigint unsigned default 3 comment '访问需要权限',
  api_path       text comment '路径',
  api_method     text comment '请求方式',
  api_protocol   varchar(63)     default null comment '请求协议',
  index (permissions_id) comment '外联 权限'
) default charset = utf8mb4,
  comment 'api';
call add_base_struct('api');


create table if not exists api_call_record
(
  api_id          bigint unsigned not null comment 'api',
  device_code     text            null comment '设备 id, 浏览器为 agent',
  req_ip          varchar(63)     null comment '请求 ip',
  login_ip        varchar(63)     null comment '登录 ip',
  resp_code       int             null comment '响应码',
  resp_result_enc text comment '请求结果',
  index (api_id) comment '外联 api'
) default charset = utf8mb4, comment 'API请求记录';
call add_base_struct('api_call_record');


create table if not exists attachment
(
  meta_name varchar(127)    null comment '原始名称',
  save_name varchar(127) comment '存储后名称',
  base_url  varchar(255)    null comment '基本url',
  url_name  varchar(127)    null comment '资源路径名称',
  url_doc   varchar(255) comment '资源路径描述',
  url_id    bigint unsigned null comment '根路径链接路径自连接id',
  att_type  int             not null comment '附件类别（URL、附件）',
  size      bigint unsigned default null comment '文件大小',
  mime_type varchar(63) comment 'MIME TYPE',
  index (url_id) comment '自连接 id',
  index (meta_name) comment '原始名称经常搜索',
  index (base_url) comment '根路径经常检索',
  index (att_type) comment '附件类型经常检索',
  index (mime_type) comment '媒体类型经常检索'
) default charset = utf8mb4, comment '文件';
call add_base_struct('attachment');


create table if not exists address
(
  code         varchar(255) comment '代码',
  name         varchar(127) comment '名称', # 中国最长的地名是新疆维吾尔自治区昌吉回族自治州木垒哈萨克自治县大南沟乌孜别克族乡
  year_version varchar(15)  null comment '年份版本号',
  level        int     default 0 comment '级别 0 为国家',
  center       varchar(255) null comment '定位',
  leaf         boolean default false comment '是否为终结地址（如市辖区）',
  unique (code) comment '行政区代码唯一',
  index (name) comment '名称经常检索'
) default charset = utf8mb4,comment '行政区代码';
call add_base_struct('address');
call add_presort_tree_struct('address');
insert into address(id, level, code, name, rln, rrn, tgi, center)
values (0, 0, '000000000000', '', 1, 2, 0, null);


create table if not exists address_details
(
  address_id      bigint unsigned not null comment '地址id',
  user_id         bigint unsigned null comment '用户 id',
  phone           varchar(127) comment '联系电话',
  name            varchar(255) comment '联系人名称',
  address_code    varchar(31)     not null comment '地址代码',
  address_details text            not null comment '地址详情',
  center          varchar(255) comment '定位',
  index (address_id) comment '外联 地址',
  index (user_id) comment '外联 用户',
  index (address_code) comment '外联 地址代码'
) default charset = utf8mb4,comment '地址详情';
call add_base_struct('address_details');


create table if not exists table_row_delete_record
(
  table_names     varchar(127)    null comment '表名',
  user_id         bigint unsigned null comment '删除用户id',
  user_account    varchar(255)    null comment '删除用户账户',
  delete_datetime datetime default now() comment '删除时间',
  entity          json            not null comment '删除实体',
  index (table_names) comment '表名经常查询',
  index (user_account) comment '用户账户经常查询',
  index (user_id) comment '外联 用户'
) default charset = utf8mb4,comment '数据删除记录';
call add_base_struct('table_row_delete_record');


create table if not exists table_row_change_record
(
  type                     boolean         not null comment '变更类型：插入：true，修改：false',
  table_names              varchar(127)    null comment '表名',
  create_user_id           bigint unsigned null comment '创建用户id',
  create_user_account      char(255)       null comment '创建用户账户',
  create_datetime          datetime        null comment '创建时间',
  create_entity            json            null comment '创建实体',
  last_modify_user_id      bigint unsigned null comment '最后修改用户id',
  last_modify_user_account char(255)       null comment '最后修改用户账户',
  last_modify_datetime     datetime        null comment '最后修改时间',
  last_modify_entity       json            not null comment '最后修改实体',
  index (table_names) comment '表名经常查询',
  index (create_user_account) comment '创建账户经常查询',
  index (last_modify_user_account) comment '最后修改用户账户经常查询',
  index (create_user_id) comment '外联 用户',
  index (last_modify_user_id) comment '外联 用户'
) default charset = utf8mb4,comment '数据变更记录';
call add_base_struct('table_row_change_record');
