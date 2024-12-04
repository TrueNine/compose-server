create
    table
        if not exists db_test_merge_table(
            id bigint primary key not null,
            ldf bool default null,
            crd timestamp default now(),
            mrd timestamp default now(),
            rlv bigint default 0,
            name varchar default null,
            age int default null
        )
