create
    table
    if not exists dynamic_form_group
(
    name varchar(127) unique not null,
    doc  text default null
);

select add_base_struct('dynamic_form_group');
