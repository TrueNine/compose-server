# Flyway Migration Shared

## 概述

`flyway-migration-shared` 模块定义了 Compose Server 框架中数据库迁移的通用规范和实现要求。该模块为不同数据库系统（PostgreSQL、MySQL 等）提供统一的数据库结构管理函数/存储过程的实现标准。

## `ct_idx` 函数/存储过程

为指定表的指定列创建索引（如果列存在且索引不存在）。支持幂等操作，可重复调用而不产生错误。

**参数：**

- `tab_name`: 表名 (varchar(128))
- `col_name`: 列名 (varchar(255))

**实现逻辑：**

1. 检查指定表中是否存在指定列
2. 如果列存在，检查是否已有对应索引
3. 仅在列存在且索引不存在时创建索引
4. 索引命名规则：`{col_name}_idx`

**核心sql操作：**

- 列存在性检查：`select column_name from information_schema.columns where table_name = ? and column_name = ?`
- 索引存在性检查：`select index_name from information_schema.statistics where table_name = ? and index_name = ?`
- 索引创建：`create index {col_name}_idx on {tab_name} ({col_name})`

## `add_base_struct` 函数/存储过程

**功能：** 为表添加 Compose Server 标准基础字段

**参数：**

- `tab_name`: 表名 (VARCHAR(128))

**标准字段：**

- `id`: 主键字段 (BIGINT NOT NULL PRIMARY KEY)，默认值为非自增的 int64 值类型，**不可设置任何自增策略，应由程序自行处理**
- `rlv`: 行乐观锁版本号 `Row Lock Version` (INT DEFAULT 0)，默认值为 0 的 int32 值类型
- `crd`: 创建时间 `Created Row Datetime` (TIMESTAMP DEFAULT CURRENT_TIMESTAMP)，与时区无关的毫秒时间戳，默认值为插入表记录的时间戳
- `mrd`: 修改时间 `Modify Row Datetime` (TIMESTAMP DEFAULT null)，与时区无关的毫秒时间戳，默认为 null
- `ldf`: 逻辑删除标记 `Logic Delete Flag` (TIMESTAMP NULL DEFAULT NULL)

**实现要求：**

- 检查字段是否已存在，避免重复添加
- 对于已有数据的表，需要妥善处理 ID 字段的添加
  - PostgreSQL: 使用临时序列为现有数据生成 ID
  - MySQL: 使用 AUTO_INCREMENT 特性
- 支持幂等操作

## `rm_base_struct` 函数/存储过程

**功能：** 移除表的基础结构字段

**参数：**

- `tab_name`: 表名 (VARCHAR(128))

**实现要求：**

- 按正确顺序移除字段（先移除约束，再移除字段）
- 安全处理主键约束的删除
- 支持幂等操作

## `add_tree_struct` 函数/存储过程

**功能：** 为表添加树形结构支持字段

**参数：**

- `tab_name`: 表名 (VARCHAR(128))

**树结构字段：**

- `rpi`: 父节点 ID `Row Parent Id` (BIGINT DEFAULT NULL)

**实现要求：**

- 为 `rpi` 字段创建索引
- 支持幂等操作

## `add_presort_tree_struct` 函数/存储过程

**功能：** 为表添加预排序树结构支持字段

**参数：**

- `tab_name`: 表名 (VARCHAR(128))

**预排序树字段：**

- `rpi`: 父节点 ID `Row Parent Id` (BIGINT DEFAULT NULL)
- `rln`: 左节点编号 `Row Left Node` (BIGINT DEFAULT 1)
- `rrn`: 右节点编号 `Row Right Node` (BIGINT DEFAULT 2)

**实现要求：**

- 为所有字段创建相应索引
- 支持嵌套集合模型的树形结构
- 支持幂等操作

## `all_to_nullable` 函数/存储过程

**功能：** 将表中所有非主键字段设置为可空

**参数：**

- `tab_name`: 表名 (VARCHAR(128))

**实现要求：**

- 排除主键字段
- 移除字段的 NOT NULL 约束
- 移除字段的默认值
- 支持幂等操作

## 实现规范

### 通用要求

1. **幂等性**： 所有函数/存储过程必须支持重复执行而不产生错误
2. **安全性**： 使用参数化查询，防止 SQL 注入
3. **错误处理**： 妥善处理异常情况，提供有意义的错误信息
4. **性能**： 在操作前检查对象是否存在，避免不必要的操作
5. **版本包含**： add 函数 和 rm 函数 应当出现在同一版本 .sql 文件中

### 数据库特定要求

#### PostgreSQL

- 使用 PL/pgSQL 语言
- 使用 `format()` 函数进行动态 SQL 构建
- 使用 `%I` 标识符转义防止注入
- 支持数组操作和循环结构

#### Postgresql

**功能：** 提供常用数据类型之间的隐式转换

**支持的转换：**

- json ↔ varchar
- bigint ↔ varchar

**实现要求：**

- 使用 `with inout as implicit` 语法
- 先删除已存在的转换，再创建新的

#### MySQL

- 使用存储过程语法
- 使用 `delimiter` 处理语句分隔符
- 使用预处理语句执行动态 SQL
- 使用 `information_schema` 进行元数据查询

### 命名约定

- 函数/存储过程名称使用下划线分隔的小写字母
- 索引命名：`{column_name}_idx`
- 约束命名遵循数据库默认规则
- 临时对象使用 `temp_` 前缀
