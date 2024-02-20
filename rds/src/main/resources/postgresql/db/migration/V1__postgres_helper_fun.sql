-- 预排序树结构
CREATE
    OR replace FUNCTION add_presort_tree_struct(
        tab_name VARCHAR(128)
    ) RETURNS void AS $$ DECLARE existing_column_name text;

BEGIN -- rpi
SELECT
    column_name INTO
        existing_column_name
    FROM
        information_schema.columns
    WHERE
        table_name = tab_name
        AND column_name = 'rpi';

IF existing_column_name IS NULL THEN EXECUTE format(
    'alter table %I add column rpi bigint default null;',
    tab_name
);

EXECUTE format(
    'create index on %I(rpi);',
    tab_name
);
END IF;

-- rln
SELECT
    column_name INTO
        existing_column_name
    FROM
        information_schema.columns
    WHERE
        table_name = tab_name
        AND column_name = 'rln';

IF existing_column_name IS NULL THEN EXECUTE format(
    'alter table %I add column rln bigint default 1;',
    tab_name
);

EXECUTE format(
    'create index on %I(rln);',
    tab_name
);
END IF;

-- rrn
SELECT
    column_name INTO
        existing_column_name
    FROM
        information_schema.columns
    WHERE
        table_name = tab_name
        AND column_name = 'rrn';

IF existing_column_name IS NULL THEN EXECUTE format(
    'alter table %I add column rrn bigint default 2;',
    tab_name
);

EXECUTE format(
    'create index on %I(rrn);',
    tab_name
);
END IF;

-- nlv
SELECT
    column_name INTO
        existing_column_name
    FROM
        information_schema.columns
    WHERE
        table_name = tab_name
        AND column_name = 'nlv';

IF existing_column_name IS NULL THEN EXECUTE format(
    'alter table %I add column nlv bigint default 0;',
    tab_name
);
END IF;

-- tgi
SELECT
    column_name INTO
        existing_column_name
    FROM
        information_schema.columns
    WHERE
        table_name = tab_name
        AND column_name = 'tgi';

IF existing_column_name IS NULL THEN EXECUTE format(
    'alter table %I add column tgi varchar(64) default ''0''::varchar;',
    tab_name
);

EXECUTE format(
    'create index on %I(tgi);',
    tab_name
);
END IF;
END $$ LANGUAGE plpgsql;

-- 基础表结构
CREATE
    OR replace FUNCTION add_base_struct(
        tab_name VARCHAR(128)
    ) RETURNS void AS $$ DECLARE existing_column_name text;

BEGIN -- id
SELECT
    column_name INTO
        existing_column_name
    FROM
        information_schema.columns
    WHERE
        table_name = tab_name
        AND column_name = 'id';

IF existing_column_name IS NULL THEN EXECUTE format(
    'alter table %I add column id bigint not null;',
    tab_name
);

EXECUTE format(
    'alter table %I add primary key (id);',
    tab_name
);
END IF;

-- rlv
SELECT
    column_name INTO
        existing_column_name
    FROM
        information_schema.columns
    WHERE
        table_name = tab_name
        AND column_name = 'rlv';

IF existing_column_name IS NULL THEN EXECUTE format(
    'alter table %I add column rlv bigint default 0;',
    tab_name
);
END IF;

-- crd
SELECT
    column_name INTO
        existing_column_name
    FROM
        information_schema.columns
    WHERE
        table_name = tab_name
        AND column_name = 'crd';

IF existing_column_name IS NULL THEN EXECUTE format(
    'alter table %I add column crd timestamp default now();',
    tab_name
);
END IF;

-- mrd
SELECT
    column_name INTO
        existing_column_name
    FROM
        information_schema.columns
    WHERE
        table_name = tab_name
        AND column_name = 'mrd';

IF existing_column_name IS NULL THEN EXECUTE format(
    'alter table %I add column mrd timestamp default now();',
    tab_name
);
END IF;

-- ldf
SELECT
    column_name INTO
        existing_column_name
    FROM
        information_schema.columns
    WHERE
        table_name = tab_name
        AND column_name = 'ldf';

IF existing_column_name IS NULL THEN EXECUTE format(
    'alter table %I add column ldf bool default null;',
    tab_name
);
END IF;
END;

$$ LANGUAGE plpgsql;
