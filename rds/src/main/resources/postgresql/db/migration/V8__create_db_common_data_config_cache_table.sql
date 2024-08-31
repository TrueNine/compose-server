create
    table
        if not exists common_kv_config_db_cache(
            k varchar(255) not null unique,
            v json default null
        );

select
    add_base_struct('common_kv_config_db_cache');
