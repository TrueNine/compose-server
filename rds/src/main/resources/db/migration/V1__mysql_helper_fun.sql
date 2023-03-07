# 设置时区为 +8:00
SET TIME_ZONE = '+8:00';
SET CHARSET utf8mb4;
FLUSH PRIVILEGES;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

# 基础表字段
DROP PROCEDURE IF EXISTS `base_tab`;
DELIMITER $$
CREATE PROCEDURE `base_tab`(IN tab_name VARCHAR(128))
BEGIN
  SET @after = CONCAT(
    'ALTER TABLE',
    ' `',
    `tab_name`,
    '` ',
    'ADD `id` BIGINT UNSIGNED PRIMARY KEY COMMENT \'主键\',',
    'ADD `cct` DATETIME DEFAULT NOW() COMMENT \'字段创建时间 column create time\',',
    'ADD `ccb` BIGINT UNSIGNED DEFAULT 0 NOT NULL COMMENT \'创建用户 column create by\',',
    'ADD `cmt` DATETIME DEFAULT NOW() COMMENT \'字段修改时间 column modify time\',',
    'ADD `cmb` BIGINT UNSIGNED DEFAULT 0 COMMENT \'修改用户 column modify by\',',
    'ADD `clv` BIGINT UNSIGNED DEFAULT 0 COMMENT \'乐观锁版本号 column lock version\', ',
    'ADD `ldf` BOOLEAN DEFAULT FALSE COMMENT \'逻辑删除标志 logic delete flag\',',
    'ADD `cti` BIGINT UNSIGNED DEFAULT 0 COMMENT \'多租户id column tenant id\', ',
    'ENGINE = InnoDB,',
    'DEFAULT CHARSET = utf8mb4,',
    'AUTO_INCREMENT = 100;'
    );
  SET @statement = CONCAT(@after);
  PREPARE state
    FROM @statement;
  EXECUTE state;
END $$
DELIMITER ;

# 预排序树结构
DROP PROCEDURE IF EXISTS `presort_tree_tab`;
DELIMITER $$
CREATE PROCEDURE `presort_tree_tab`(IN tab_name VARCHAR(128))
BEGIN
  SET @after = CONCAT(
    'ALTER TABLE',
    ' `',
    `tab_name`,
    '` ',
    'ADD `cpi` BIGINT UNSIGNED DEFAULT NULL COMMENT \'父节点id column parent id\',',
    'ADD `cgu` BIGINT UNSIGNED NOT NULL COMMENT \'树组织id column group uni id\',',
    'ADD `cln` BIGINT UNSIGNED DEFAULT 1 COMMENT \'左节点 column left node\',',
    'ADD `crn` BIGINT UNSIGNED DEFAULT 2 COMMENT \'右节点 column right node\';'
    );
  SET @statement = CONCAT(@after);
  PREPARE state
    FROM @statement;
  EXECUTE state;
END $$
DELIMITER ;

# 预排序树结构
DROP PROCEDURE IF EXISTS `reference_type`;
DELIMITER $$
CREATE PROCEDURE `reference_type`(
  IN tab_name VARCHAR(128),
  IN typ_comm VARCHAR(100)
)
BEGIN
  SET @after = CONCAT(
    'ALTER TABLE',
    ' `',
    `tab_name`,
    '` ',
    'ADD `typ` BIGINT UNSIGNED DEFAULT 0 COMMENT \'外键类型描述符 type, 用于描述: ',
    `typ_comm`,
    '\',',
    'ADD INDEX(`typ`),',
    'ADD `ari` BIGINT UNSIGNED COMMENT \'任意外键 any reference id\',',
    'ADD INDEX(`ari`)',
    ';'
    );
  SET @statement = CONCAT(@after);
  PREPARE state
    FROM @statement;
  EXECUTE state;
END $$
DELIMITER ;
