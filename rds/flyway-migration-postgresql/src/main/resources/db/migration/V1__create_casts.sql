-- json to varchar
drop
    cast if exists(
        json as character varying
    );

create
    cast(
        json as character varying
    ) with inout as implicit;

drop
    cast if exists(
        character varying as json
    );

create
    cast(
        character varying as json
    ) with inout as implicit;

-- bigint to varchar
drop
    cast if exists(
        bigint as character varying
    );

create
    cast(
        bigint as character varying
    ) with inout as implicit;

drop
    cast if exists(
        character varying as bigint
    );

create
    cast(
        character varying as bigint
    ) with inout as implicit;
