create table if not exists idcard_2
(
  user_id            bigint       not null,
  user_info_id       bigint       not null,
  address_details_id bigint       default null,
  name               varchar(255) not null,
  gender             integer      default null,
  code               varchar(64)  not null,
  birthday           date         default null,
  ethnic_group       varchar(127) default null,
  expire_date        date         default null,
  issue_organ        varchar(255) default null
);
comment on table idcard_2 is '身份证2代';
select add_base_struct('idcard_2');


create table if not exists dis_cert_2
(
  user_id            bigint       default null,
  user_info_id       bigint       default null,
  name               varchar(255) not null,
  gender             integer      default null,
  code               varchar(64)  default null,
  type               integer      not null,
  level              integer      not null,
  issue_date         date         default null,
  expire_time        date         default null,
  address_details_id bigint       default null,
  guardian           varchar(255) default null,
  guardian_phone     varchar(127) default null,
  birthday           date         default null
);
comment on table dis_cert_2 is '残疾证2代';
select add_base_struct('dis_cert_2');


create table if not exists household_cert
(
  user_id                        bigint        default null,
  user_info_id                   bigint        default null,
  household_type                 integer       default null,
  household_primary_name         varchar(255)  default null,
  code                           varchar(255)  default null,
  address_details_id             bigint        default null,
  issue_organ                    varchar(255)  default null,
  name                           varchar(255) not null,
  old_name                       varchar(255)  default null,
  relationship                   integer       default null,
  gender                         integer       default null,
  ethnic_group                   varchar(127)  default null,
  birthday                       date          default null,
  height                         decimal(4, 2) default null,
  blood_type                     integer       default null,
  place_birth_address_details_id bigint        default null,
  origin_address_details_id      bigint        default null,
  idcard_code                    varchar(255) not null,
  education_level                integer       default null,
  occupation                     varchar(255)  default null,
  military_service_status        varchar(255)  default null,
  service_address_details_id     bigint        default null,
  issue_date                     date          default null
);
comment on table household_cert is '户口登记卡';
select add_base_struct('household_cert');


create table bank_card
(
  user_id               bigint       default null,
  user_info_id          bigint       default null,
  code                  varchar(255) not null,
  country               varchar(255) default null,
  bank_group            integer      default null,
  bank_type             integer      default null,
  issue_address_details text         default null
);
comment on table bank_card is '银行卡';
select add_base_struct('bank_card');
