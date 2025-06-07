create
    table
        if not exists test_entity(
            id bigserial primary key,
            name varchar default null,
            "value" varchar default null
        );