# 设置时区为 +8:00
SET TIME_ZONE = '+8:00';
SET CHARSET utf8mb4;
FLUSH PRIVILEGES;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

# 基础表字段
DROP PROCEDURE IF EXISTS add_base_struct;
DELIMITER $$
CREATE PROCEDURE add_base_struct(IN tab_name VARCHAR(128))
BEGIN
  SET @after = CONCAT(
    'ALTER TABLE',
    ' `',
    tab_name,
    '` ',
    'ADD `id` BIGINT UNSIGNED PRIMARY KEY COMMENT \'主键\',',
    'ADD `rct` DATETIME DEFAULT NOW() COMMENT \'字段创建时间 row create time\',',
    'ADD `rcb` BIGINT UNSIGNED DEFAULT 0 NOT NULL COMMENT \'创建用户 row create by\',',
    'ADD `rmt` DATETIME DEFAULT NOW() COMMENT \'字段修改时间 row modify time\',',
    'ADD `rmb` BIGINT UNSIGNED DEFAULT 0 COMMENT \'修改用户 row modify by\',',
    'ADD `rlv` BIGINT UNSIGNED DEFAULT 0 COMMENT \'乐观锁版本号 row lock version\', ',
    'ADD `ldf` BOOLEAN DEFAULT FALSE COMMENT \'逻辑删除标志 logic delete flag\',',
    'ADD `rti` BIGINT UNSIGNED DEFAULT 0 COMMENT \'多租户id row tenant id\', ',
    'ENGINE = InnoDB,',
    'DEFAULT CHARSET = utf8mb4,',
    'AUTO_INCREMENT = 100;'
    );
  SET @statement = CONCAT(@after);
  PREPARE state
    FROM @statement;

  SET @tbl_exist = (SELECT COUNT(1)
                    FROM information_schema.tables
                    WHERE table_schema = (SELECT DATABASE())
                      AND table_name = tab_name);
  SET @col_exists = (SELECT COUNT(1)
                     FROM information_schema.columns
                     WHERE table_schema = (SELECT DATABASE())
                       AND table_name = tab_name
                       AND column_name IN ('id', 'rct', 'rcb'));
  IF ((@tbl_exist) > 0 AND (@col_exists) <= 0) THEN
    EXECUTE state;
  END IF;
END $$
DELIMITER ;

# 预排序树结构
DROP PROCEDURE IF EXISTS add_presort_tree_struct;
DELIMITER $$
CREATE PROCEDURE add_presort_tree_struct(IN tab_name VARCHAR(128))
BEGIN
  SET @after = CONCAT(
    'ALTER TABLE',
    ' `',
    tab_name,
    '` ',
    'ADD `rpi` BIGINT UNSIGNED DEFAULT NULL COMMENT \'父节点id parent id\',',
    'ADD `rln` BIGINT UNSIGNED DEFAULT 1 COMMENT \'左节点 row left node\',',
    'ADD `rrn` BIGINT UNSIGNED DEFAULT 2 COMMENT \'右节点 row right node\',',
    'ADD INDEX(`rln`),',
    'ADD INDEX(`rrn`),',
    'ADD INDEX(`rpi`);'
    );
  SET @statement = CONCAT(@after);
  PREPARE state
    FROM @statement;

  SET @tbl_exist = (SELECT COUNT(1)
                    FROM information_schema.tables
                    WHERE table_schema = (SELECT DATABASE())
                      AND table_name = tab_name);
  SET @col_exists = (SELECT COUNT(1)
                     FROM information_schema.columns
                     WHERE table_schema = (SELECT DATABASE())
                       AND table_name = tab_name
                       AND column_name IN ('rpi', 'rln', 'rrn'));
  IF ((@tbl_exist) > 0 AND (@col_exists) <= 0) THEN
    EXECUTE state;
  END IF;
END $$
DELIMITER ;

# 任意外键类型结构
DROP PROCEDURE IF EXISTS add_reference_any_type_struct;
DELIMITER $$
CREATE PROCEDURE add_reference_any_type_struct(
  IN tab_name VARCHAR(128),
  IN typ_comm VARCHAR(100)
)
BEGIN
  SET @after = CONCAT(
    'ALTER TABLE',
    ' `',
    tab_name,
    '` ',
    'ADD `typ` BIGINT UNSIGNED DEFAULT 0 COMMENT \'外键类型描述符 type, 用于描述: ',
    typ_comm,
    '\',',
    'ADD INDEX(`typ`),',
    'ADD `ari` BIGINT UNSIGNED COMMENT \'任意外键 any reference id\',',
    'ADD INDEX(`ari`)',
    ';'
    );
  SET @statement = CONCAT(@after);
  PREPARE state
    FROM @statement;

  SET @tbl_exist = (SELECT COUNT(1)
                    FROM information_schema.tables
                    WHERE table_schema = (SELECT DATABASE())
                      AND table_name = tab_name);
  SET @col_exists = (SELECT COUNT(1)
                     FROM information_schema.columns
                     WHERE table_schema = (SELECT DATABASE())
                       AND table_name = tab_name
                       AND column_name IN ('ari', 'typ'));
  IF ((@tbl_exist) > 0 AND (@col_exists) <= 0) THEN
    EXECUTE state;
  END IF;
END $$
DELIMITER ;
