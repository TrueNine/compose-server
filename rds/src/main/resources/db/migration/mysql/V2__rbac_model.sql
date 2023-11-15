create table if not exists user
(
  account         varchar(255) not null comment '账号',
  nick_name       varchar(2047) comment '呢称',
  doc             text comment '描述',
  pwd_enc         varchar(2047) default null comment '密码',
  ban_time        datetime      default null comment '被封禁结束时间',
  last_login_time datetime      default now() comment '最后请求时间',
  unique (account) comment '账号唯一'
) default charset = utf8mb4,comment '用户';
call add_base_struct('user');

insert into user(id, account, nick_name, pwd_enc, last_login_time)
values (0, 'root', 'ROOT', '$2a$14$4.QaPjTjIPILS5EnK3q3yu/OoKiuVykyLiDOIVIFy0ypbs9CL7wNi', now()),
       (1, 'usr', 'USR', '$2a$14$Rfvt1A9RVEgp47pTTiT1KeKSJt14CtSJsv2iSggLTQJcgUHA5o0sa', now());

create table if not exists user_info
(
  user_id            bigint unsigned not null comment '用户',
  avatar_img_id      bigint unsigned comment '用户头像',
  first_name         varchar(4095) comment '姓',
  last_name          varchar(4095) comment '名',
  email              varchar(255) comment '邮箱',
  birthday           datetime comment '生日',
  address_details_id bigint unsigned comment '地址',
  phone              varchar(255) comment '电话号码',
  id_card            varchar(255) comment '身份证',
  gender             tinyint default 2 comment ' 性别：0女，1难，2未知',
  wechat_openid varchar(255) null comment '微信个人 openId',
  wechat_authid varchar(127) comment '微信自定义登录id',
  unique (phone) comment '电话唯一',
  unique (id_card) comment '身份证唯一',
  unique (wechat_openid) comment '微信 openId唯一',
  unique (wechat_authid) comment '微信自定义登录id 唯一',
  index (wechat_openid) comment '微信 openId 经常查询',
  index (wechat_authid) comment '微信自定义登录id经常查询',
  index (user_id) comment '外联 用户',
  index (address_details_id) comment '外联 地址详情',
  index (avatar_img_id) comment '外联 文件'
) default charset = utf8mb4, comment '用户信息';
call add_base_struct('user_info');
insert into user_info(id, user_id, first_name, last_name, email, birthday, phone, gender)
values (0, 0, 'R', 'OOT', 'gg@gmail.com', '1997-11-04', '15555555551', 1),
       (1, 1, 'U', 'SR', 'gg@gmail.com', '1997-11-04', '15555555552', 1);

create table if not exists role
(
  name varchar(255) comment '角色名称',
  doc  text comment '角色描述'
) default charset = utf8mb4, comment '角色';
call add_base_struct('role');
insert into role (id, name, doc)
values (0, 'ROOT', '默认超级管理员角色，务必不要删除'),
       (1, 'USER', '默认USER角色，务必不要删除');


create table if not exists permissions
(
  name varchar(255) comment '权限名',
  doc  text comment '权限描述'
) default charset = utf8mb4, comment '权限';
call add_base_struct('permissions');
insert into permissions(id, name, doc)
values (0, 'ROOT', '默认ROOT权限，务必不要删除'),
       (1, 'USER', '默认USER权限，务必不要删除');


create table if not exists role_group
(
  name varchar(255) comment '名称',
  doc  text comment '描述'
) default charset = utf8mb4, comment '角色组';
call add_base_struct('role_group');
insert into role_group(id, name, doc)
values (0, 'ROOT', '默认ROOT角色组，务必不要删除'),
       (1, 'USER', '默认USER角色组，务必不要删除');


create table if not exists role_permissions
(
  role_id        bigint unsigned comment '角色',
  permissions_id bigint unsigned comment '权限',
  index (role_id) comment '外联 角色',
  index (permissions_id) comment '外联 权限'
) default charset = utf8mb4, comment '角色  权限';
call add_base_struct('role_permissions');
insert into role_permissions(id, role_id, permissions_id)
values (0, 0, 0),
       (1, 0, 1),
       (2, 1, 1);


create table if not exists role_group_role
(
  role_group_id bigint unsigned comment '用户组',
  role_id       bigint unsigned comment '角色',
  index (role_group_id) comment '外联 角色组',
  index (role_id) comment '外联 角色'
) default charset = utf8mb4,comment '角色组  角色';
call add_base_struct('role_group_role');
insert into role_group_role(id, role_group_id, role_id)
values (0, 0, 0),
       (1, 0, 1),
       (2, 1, 1);

create table if not exists user_role_group
(
  user_id       bigint unsigned comment '用户',
  role_group_id bigint unsigned comment '权限组',
  index (user_id) comment '外联 用户',
  index (role_group_id) comment '外联 角色组'
) default charset = utf8mb4, comment '用户  角色组';
call add_base_struct('user_role_group');
insert into user_role_group(id, user_id, role_group_id)
values (0, 0, 0),
       (1, 0, 1),
       (2, 1, 1);


create table if not exists dept
(
  name varchar(255)  not null comment '名称',
  doc  text null comment '描述'
) default charset = utf8mb4,comment '部门';
call add_base_struct('dept');


create table if not exists user_dept
(
  user_id bigint unsigned not null comment '用户 id',
  dept_id bigint unsigned not null comment '部门 id'
) default charset = utf8mb4,comment '用户  部门';
call add_base_struct('user_dept');
call add_presort_tree_struct('user_dept');
