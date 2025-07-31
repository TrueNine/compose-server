-- Base structure procedures for MySQL
DELIMITER
$$

drop procedure if exists add_base_struct$$
create procedure add_base_struct(
    in tab_name varchar (128)
)
begin
    declare
col_count int default 0;
    declare
sql_stmt TEXT;

    -- Add id column if not exists
select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'id';
IF
col_count = 0 then
        set sql_stmt = CONCAT('ALTER TABLE ', tab_name, ' ADD COLUMN id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end IF;

    -- Add rlv column
select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'rlv';
IF
col_count = 0 then
        set sql_stmt = CONCAT('ALTER TABLE ', tab_name, ' ADD COLUMN rlv INT DEFAULT 0');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end IF;

    -- Add crd column
select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'crd';
IF
col_count = 0 then
        set sql_stmt = CONCAT('ALTER TABLE ', tab_name, ' ADD COLUMN crd TIMESTAMP DEFAULT CURRENT_TIMESTAMP');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end IF;

    -- Add mrd column
select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'mrd';
IF
col_count = 0 then
        set sql_stmt = CONCAT('ALTER TABLE ', tab_name, ' ADD COLUMN mrd TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end IF;

    -- Add ldf column
select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'ldf';
IF
col_count = 0 then
        set sql_stmt = CONCAT('ALTER TABLE ', tab_name, ' ADD COLUMN ldf TIMESTAMP NULL DEFAULT NULL');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end IF;
END$$

drop procedure if exists rm_base_struct$$
create procedure rm_base_struct(
    in tab_name varchar (128)
)
begin
    declare
col_count int default 0;
    declare
sql_stmt TEXT;

    -- Remove columns in reverse order
select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'ldf';
IF
col_count > 0 then
        set sql_stmt = CONCAT('ALTER TABLE ', tab_name, ' DROP COLUMN ldf');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end IF;

select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'mrd';
IF
col_count > 0 then
        set sql_stmt = CONCAT('ALTER TABLE ', tab_name, ' DROP COLUMN mrd');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end IF;

select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'crd';
IF
col_count > 0 then
        set sql_stmt = CONCAT('ALTER TABLE ', tab_name, ' DROP COLUMN crd');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end IF;

select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'rlv';
IF
col_count > 0 then
        set sql_stmt = CONCAT('ALTER TABLE ', tab_name, ' DROP COLUMN rlv');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end IF;

select count(*)
into col_count
from information_schema.columns
where table_schema = database()
  and table_name = tab_name
  and column_name = 'id';
IF
col_count > 0 then
        set sql_stmt = CONCAT('ALTER TABLE ', tab_name, ' DROP PRIMARY KEY, DROP COLUMN id');
        set
@sql = sql_stmt;
prepare stmt from @sql;
execute stmt;
deallocate prepare stmt;
end IF;
END$$

DELIMITER ;
