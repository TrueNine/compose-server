create
    table
        if not exists dynamic_form(
            form_id varchar(127) not null,
            version varchar(5) not null,
            value_type varchar(127) not null,
            component_type varchar(127) not null,
            index int not null,
            label varchar(127) default null,
            placeholder varchar(127) default null,
            group_index int default null,
            readonly boolean default null,
            required boolean default null,
            description text default null,
            default_value json default null,
            options json default null,
            rules json default null
        );

select
    add_base_struct('dynamic_form');
