-- 设置时区为 +8:00
set time zone interval '+8:00';

-- 创建 bitint 到 string 的隐式转换
drop cast if exists (character varying as bigint);
drop cast if exists (bigint as character varying);
create cast (character varying as bigint) with inout as implicit;
create cast (bigint as character varying) with inout as implicit;

-- 预排序树结构
create or replace function add_presort_tree_struct(tab_name varchar(128))
  returns void as
$$
begin
  execute format('ALTER TABLE %I ADD COLUMN rpi BIGINT DEFAULT NULL;', tab_name);
  execute format('ALTER TABLE %I ADD COLUMN rln BIGINT DEFAULT 1;', tab_name);
  execute format('ALTER TABLE %I ADD COLUMN rrn BIGINT DEFAULT 2;', tab_name);
  execute format('ALTER TABLE %I ADD COLUMN nlv BIGINT DEFAULT 0;', tab_name);
  execute format('alter table %I add column tgi varchar(64) default ''0''::varchar;', tab_name);
  execute format('CREATE INDEX ON %I(rln);', tab_name);
  execute format('CREATE INDEX ON %I(rrn);', tab_name);
  execute format('CREATE INDEX ON %I(tgi);', tab_name);
  execute format('CREATE INDEX ON %I(rpi);', tab_name);
end
$$ language plpgsql;

-- 基础表结构
create or replace function add_base_struct(tab_name varchar(128))
  returns void as
$$
begin
  execute format('ALTER TABLE %I ADD COLUMN id BIGINT NOT NULL;', tab_name);
  execute format('ALTER TABLE %I ADD PRIMARY KEY (id);', tab_name);
  execute format('ALTER TABLE %I ADD COLUMN rlv BIGINT DEFAULT 0;', tab_name);
  execute format('ALTER TABLE %I ADD COLUMN ldf BOOLEAN DEFAULT FALSE;', tab_name);
end;
$$ language plpgsql;

-- 任意外键约束
create or replace function add_reference_any_type_struct(
  tab_name varchar(128),
  typ_comm varchar(128)
) returns void as
$$
begin
  execute format('ALTER TABLE %I ADD COLUMN typ INTEGER DEFAULT 0;', tab_name, typ_comm);
  execute format('ALTER TABLE %I ADD COLUMN ari BIGINT;', tab_name);
  execute format('CREATE INDEX idx_typ ON %I (typ);', tab_name);
  execute format('CREATE INDEX idx_ari ON %I (ari);', tab_name);
end;
$$ language plpgsql;
