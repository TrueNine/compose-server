create
    table
    if not exists dynamic_form_value
(
    form_group_id      bigint not null,
    api_for            varchar(127) default null,
    platform_for       varchar(127) default null,
    input_user_id      bigint       default null,
    input_user_info_id bigint       default null,
    input_datetime     bigint not null,
    audit_id           bigint       default null,
    values             json         default null
);

select add_base_struct('dynamic_form_value');
