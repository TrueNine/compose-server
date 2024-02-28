create
    table
        if not exists audit(
            status integer default 0,
            create_datetime timestamp default now(),
            audit_user_id bigint default null,
            ref_id bigint default null,
            ref_type integer default null,
            audit_ip varchar(64) default null,
            audit_device_id varchar(255) default null,
            remark text default null
        );

comment on
table
    audit is '审核备注';

select
    add_base_struct('audit');

create
    index on
    audit(status);

create
    index on
    audit(audit_user_id);

create
    index on
    audit(ref_id);

create
    index on
    audit(ref_type);

create
    table
        if not exists audit_attachment(
            att_id bigint not null,
            audit_id bigint not null,
            status int default null
        );

select
    add_base_struct('audit_attachment');

create
    index on
    audit_attachment(status);

create
    index on
    audit_attachment(att_id);

create
    index on
    audit_attachment(audit_id);
