create table if not exists "user"
(
  account         varchar(255) not null,
  nick_name       varchar(2047),
  doc             text,
  pwd_enc         varchar(2047) default null,
  ban_time        timestamp     default null,
  last_login_time timestamp     default now(),
  unique (account)
);
comment on table "user" is '用户';
select add_base_struct('user');
insert into "user"(id, account, nick_name, pwd_enc, last_login_time)
values (0, 'root', 'ROOT', '$2a$14$4.QaPjTjIPILS5EnK3q3yu/OoKiuVykyLiDOIVIFy0ypbs9CL7wNi', now()),
       (1, 'usr', 'USR', '$2a$14$Rfvt1A9RVEgp47pTTiT1KeKSJt14CtSJsv2iSggLTQJcgUHA5o0sa', now());


create table if not exists user_info
(
  user_id            bigint not null,
  avatar_img_id      bigint,
  first_name         varchar(4095),
  last_name          varchar(4095),
  email              varchar(255),
  birthday           timestamp,
  address_details_id bigint,
  phone              varchar(255),
  id_card            varchar(255),
  gender             int default 2,
  wechat_openid varchar(255) null,
  wechat_authid varchar(255),
  unique (wechat_openid),
  unique (phone),
  unique (id_card)
);
comment on table user_info is '用户信息';
select add_base_struct('user_info');
create index on user_info (user_id);

create index on user_info (address_details_id);
create index on user_info (avatar_img_id);
create index on user_info (wechat_openid);
create index on user_info (wechat_authid);
insert into user_info(id, user_id, first_name, last_name, email, birthday, phone, gender)
values (0, 0, 'R', 'OOT', 'gg@gmail.com', '1997-11-04', '15555555551', 1),
       (1, 1, 'U', 'SR', 'gg@gmail.com', '1997-11-04', '15555555552', 1);

create table if not exists role
(
  name varchar(255) not null,
  doc  text
);
comment on table role is '角色';
select add_base_struct('role');
insert into role (id, name, doc)
values (0, 'ROOT', '默认超级管理员角色，务必不要删除'),
       (1, 'USER', '默认USER角色，务必不要删除');

create table if not exists permissions
(
  name varchar(255) not null,
  doc  text
);
comment on table permissions is '权限';
select add_base_struct('permissions');
insert into permissions(id, name, doc)
values (0, 'ROOT', '默认ROOT权限，务必不要删除'),
       (1, 'USER', '默认USER权限，务必不要删除');

create table if not exists role_group
(
  name varchar(255) not null,
  doc  text
);
comment on table role_group is '角色组';
select add_base_struct('role_group');
insert into role_group(id, name, doc)
values (0, 'ROOT', '默认ROOT角色组，务必不要删除'),
       (1, 'USER', '默认USER角色组，务必不要删除');


create table if not exists role_permissions
(
  role_id        bigint not null,
  permissions_id bigint not null
);
comment on table role_permissions is '角色  权限';
select add_base_struct('role_permissions');
create index on role_permissions (role_id);
create index on role_permissions (permissions_id);
insert into role_permissions(id, role_id, permissions_id)
values (0, 0, 0),
       (1, 0, 1),
       (2, 1, 1);


create table if not exists role_group_role
(
  role_group_id bigint not null,
  role_id       bigint not null
);
comment on table role_group_role is '角色组  角色';
select add_base_struct('role_group_role');
create index on role_group_role (role_group_id);
create index on role_group_role (role_id);
insert into role_group_role(id, role_group_id, role_id)
values (0, 0, 0),
       (1, 0, 1),
       (2, 1, 1);


create table if not exists user_role_group
(
  user_id       bigint not null,
  role_group_id bigint not null
);
comment on table user_role_group is '用户  角色组';
select add_base_struct('user_role_group');
create index on user_role_group (role_group_id);
create index on user_role_group (user_id);
insert into user_role_group(id, user_id, role_group_id)
values (0, 0, 0),
       (1, 0, 1),
       (2, 1, 1);

create table if not exists dept
(
  name varchar(255) not null,
  doc  text         null
);
comment on table dept is '部门';
select add_base_struct('dept');


create table if not exists user_dept
(
  user_id bigint not null,
  dept_id bigint not null
);
comment on table user_dept is '用户  部门';
select add_base_struct('user_dept');
select add_presort_tree_struct('user_dept');