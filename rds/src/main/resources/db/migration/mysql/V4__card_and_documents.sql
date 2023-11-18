create table if not exists idcard_2
(
  user_id            bigint unsigned not null,
  address_details_id bigint unsigned default null,
  name               varchar(255)    not null,
  gender             integer         default null,
  code               varchar(64)     not null,
  birthday           date            default null,
  ethnic_group       varchar(127)    default null,
  expire_date        date            default null,
  issue_organ        varchar(255)    default null
) default charset = utf8mb4, comment '身份证2代';
call add_base_struct('idcard_2');


create table if not exists disability_certificate_2
(
  user_id            bigint unsigned not null,
  name               varchar(255)    not null,
  gender             integer         default null,
  code               varchar(64)     default null,
  type               integer         not null,
  level              integer         not null,
  issue_date         date            default null,
  expire_time        date            default null,
  address_details_id bigint unsigned default null,
  guardian           varchar(255)    default null,
  guardian_phone     varchar(127)    default null,
  birthday           date            default null
) default charset = utf8mb4,comment '残疾证2代';
call add_base_struct('disability_certificate_2');


create table if not exists household_registration_card
(
  user_id                        bigint unsigned not null,
  household_type                 integer         default null,
  household_primary_name         varchar(255)    default null,
  code                           varchar(255)    default null,
  address_details_id             bigint unsigned default null,
  issue_organ                    varchar(255)    default null,
  name                           varchar(255)    not null,
  old_name                       varchar(255)    default null,
  relationship                   integer         default null,
  gender                         integer         default null,
  ethnic_group                   varchar(127)    default null,
  birthday                       date            default null,
  height                         decimal(4, 2)   default null,
  blood_type                     integer         default null,
  place_birth_address_details_id bigint unsigned default null,
  origin_address_details_id      bigint unsigned default null,
  idcard_code                    varchar(255)    not null,
  education_level                integer         default null,
  occupation                     varchar(255)    default null,
  military_service_status        varchar(255)    default null,
  service_address_details_id     bigint unsigned default null,
  issue_date                     date            default null
) default charset = utf8mb4, comment '户口登记卡';
call add_base_struct('household_registration_card');


create table bank_card
(
  user_id               bigint unsigned not null,
  code                  varchar(255)    not null,
  country               varchar(255) default null,
  bank_group            integer      default null,
  bank_type             integer      default null,
  issue_address_details text         default null
) default charset = utf8mb4, comment '银行卡';
call add_base_struct('bank_card');
