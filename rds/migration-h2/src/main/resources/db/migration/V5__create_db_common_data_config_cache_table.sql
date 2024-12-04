create
    table
        if not exists common_kv_config_db_cache(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            k varchar(255) not null unique,
            v json default null
        );
