-- 设置时区为 +8:00
SET TIME ZONE INTERVAL '+8:00';

-- 预排序树结构
CREATE OR REPLACE FUNCTION add_presort_tree_struct(tab_name VARCHAR(128))
  RETURNS VOID AS
$$
BEGIN
  EXECUTE 'ALERT TABLE ' || quote_ident(tab_name) || '
    ADD COLUMN rpi BIGINT DEFAULT NULL COMMENT ‘’父节点id parent id‘’,
    ADD COLUMN rln BIGINT DEFAULT 1 COMMENT ‘’左节点 row left node‘’,
    ADD COLUMN rrn BIGINT DEFAULT 2 COMMENT ‘’右节点 row right node‘’,
    ADD COLUMN nlv BIGINT DEFAULT 0 COMMENT ‘’节点级别 node level‘’,
    ADD COLUMN tgi VARCHAR(64) DEFAULT ‘’0‘’ COMMENT ‘’树组id tree group id‘’,
    ADD INDEX(rln),
    ADD INDEX(rrn),
    ADD INDEX(tgi),
    ADD INDEX(rpi)';
END
$$ LANGUAGE plpgsql;

-- 基础表结构
CREATE OR REPLACE FUNCTION add_base_struct(tab_name VARCHAR(128))
  RETURNS VOID AS
$$
DECLARE
  after_stmt TEXT;
  statement  TEXT;
  tbl_exist  INT;
  col_exists INT;
BEGIN
  after_stmt := CONCAT(
    'ALTER TABLE ',
    tab_name || ' ',
    'ADD COLUMN id BIGINT PRIMARY KEY COMMENT '' 主键'',',
    'ADD COLUMN rlv BIGINT DEFAULT 0 COMMENT ''乐观锁版本号 row lock version'',',
    'ADD COLUMN ldf BOOLEAN DEFAULT FALSE COMMENT ''逻辑删除标志 logic delete flag'';'
    );
  statement := CONCAT(after_stmt);
  EXECUTE statement;

  SELECT COUNT(1)
  INTO tbl_exist
  FROM information_schema.tables
  WHERE table_schema = current_database()
    AND table_name = tab_name;

  SELECT COUNT(1)
  INTO col_exists
  FROM information_schema.columns
  WHERE table_schema = current_database()
    AND table_name = tab_name
    AND column_name IN ('id', 'rct', 'rcb');

  IF (tbl_exist > 0 AND col_exists <= 0) THEN
    EXECUTE statement;
  END IF;
END;
$$ LANGUAGE plpgsql;

-- 任意外键约束
CREATE OR REPLACE FUNCTION add_reference_any_type_struct(
  tab_name VARCHAR(128),
  typ_comm VARCHAR(100)
) RETURNS VOID AS
$$
BEGIN
  EXECUTE format('ALTER TABLE %I ADD COLUMN typ INTEGER DEFAULT 0 COMMENT ''外键类型描述符 type, 用于描述: %s'';', tab_name, typ_comm);
  EXECUTE format('ALTER TABLE %I ADD COLUMN ari BIGINT COMMENT ''任意外键 any reference id'';', tab_name);
  EXECUTE format('CREATE INDEX idx_typ ON %I (typ);', tab_name);
  EXECUTE format('CREATE INDEX idx_ari ON %I (ari);', tab_name);
END;
$$ LANGUAGE plpgsql;
