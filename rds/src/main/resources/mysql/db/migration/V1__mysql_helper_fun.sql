# 设置时区为 + 8:00
SET
TIME_ZONE = '+8:00';
SET
charset utf8mb4;

flush PRIVILEGES;
SET
names utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

# 基础表字段 delimiter $$ CREATE
    PROCEDURE add_base_struct(
        IN tab_name VARCHAR(128)
    ) BEGIN
SET
    @after = concat(
        'alter table',
        tab_name,
        'add `id` bigint primary key comment \' 主键\',',
        'add `rlv` bigint default 0 comment \' 乐观锁版本号 ROW lock version\',',
        'add `ldf` boolean default false comment \' 逻辑删除标志 logic DELETE
            flag\',',
            'add `crd` datetime default now() comment \' 字段创建时间 CREATE
                ROW datetime\',',
                'add `mrd` datetime default now() comment \' 字段修改时间 MODIFY ROW datetime\',',
                'engine = InnoDB,',
                'default charset = utf8mb4,',
                'auto_increment = 100;'
    );
SET
@statement = concat(@after);

PREPARE state
FROM
@statement;
SET
@tbl_exist =(
    SELECT
        COUNT( 1 )
    FROM
        information_schema.tables
    WHERE
        table_schema =(
            SELECT
                database()
        )
        AND table_name = tab_name
);
SET
@col_exists =(
    SELECT
        COUNT( 1 )
    FROM
        information_schema.columns
    WHERE
        table_schema =(
            SELECT
                database()
        )
        AND table_name = tab_name
        AND column_name IN(
            'id',
            'rct',
            'rcb'
        )
);

IF(
    (@tbl_exist)> 0
    AND(@col_exists)<= 0
) THEN EXECUTE state;
END IF;
END $$ delimiter;

# 预排序树结构 delimiter $$ CREATE
    PROCEDURE add_presort_tree_struct(
        IN tab_name VARCHAR(128)
    ) BEGIN
SET
    @after = concat(
        'alter table',
        tab_name,
        'add `rpi` bigint default null comment \' 父节点id parent id\',',
        'add `rln` bigint default 1 comment \' 左节点 ROW LEFT node\',',
        'add `rrn` bigint default 2 comment \' 右节点 ROW RIGHT node\',',
        'add `nlv` bigint default 0 comment \' 节点级别 node level\',',
        'add `tgi` varchar(64) default \' 0 \' comment \' 树组id tree GROUP id\',',
        'add index(`rln`) comment \' 索引左节点\',',
        'add index(`rrn`) comment \' 索引右节点\',',
        'add index(`tgi`) comment \' 树组id\',',
        'add index(`rpi`) comment \' 自联 父节点\';'
    );
SET
@statement = concat(@after);

PREPARE state
FROM
@statement;
SET
@tbl_exist =(
    SELECT
        COUNT( 1 )
    FROM
        information_schema.tables
    WHERE
        table_schema =(
            SELECT
                database()
        )
        AND table_name = tab_name
);
SET
@col_exists =(
    SELECT
        COUNT( 1 )
    FROM
        information_schema.columns
    WHERE
        table_schema =(
            SELECT
                database()
        )
        AND table_name = tab_name
        AND column_name IN(
            'rpi',
            'rln',
            'rrn'
        )
);

IF(
    (@tbl_exist)> 0
    AND(@col_exists)<= 0
) THEN EXECUTE state;
END IF;
END $$ delimiter;

# 任意外键类型结构 delimiter $$ CREATE
    PROCEDURE add_reference_any_type_struct(
        IN tab_name VARCHAR(128),
        IN typ_comm VARCHAR(100)
    ) BEGIN
SET
    @after = concat(
        'alter table',
        tab_name,
        'add `typ` int default 0 comment \' 外键类型描述符 TYPE,
        用于描述:',
        typ_comm, ' \',',
        'add index(`typ`),',
        'add `ari` bigint comment \' 任意外键 ANY reference id\',',
        'add index(`ari`);'
    );
SET
@statement = concat(@after);

PREPARE state
FROM
@statement;
SET
@tbl_exist =(
    SELECT
        COUNT( 1 )
    FROM
        information_schema.tables
    WHERE
        table_schema =(
            SELECT
                database()
        )
        AND table_name = tab_name
);
SET
@col_exists =(
    SELECT
        COUNT( 1 )
    FROM
        information_schema.columns
    WHERE
        table_schema =(
            SELECT
                database()
        )
        AND table_name = tab_name
        AND column_name IN(
            'ari',
            'typ'
        )
);

IF(
    (@tbl_exist)> 0
    AND(@col_exists)<= 0
) THEN EXECUTE state;
END IF;
END $$ delimiter;
