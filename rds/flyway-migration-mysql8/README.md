# Flyway Migration MySQL8

MySQL 8 数据库迁移模块，提供 Flyway 数据库版本管理和 MySQL 特定的存储过程。

## 功能特性

- **Flyway 集成**：自动化数据库版本管理
- **MySQL 8 支持**：针对 MySQL 8.0+ 优化
- **存储过程**：提供常用的数据库操作存储过程
- **Spring Boot 集成**：无缝集成到 Spring Boot 应用

## 核心存储过程

### ct_idx(table_name, column_name)
创建索引的存储过程，支持幂等操作：
- 检查列是否存在
- 检查索引是否已存在
- 安全创建索引

### add_base_struct(table_name)
为表添加基础结构字段：
- `id`: 主键，BIGINT AUTO_INCREMENT
- `rlv`: 版本号，INT DEFAULT 0
- `crd`: 创建时间，TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- `mrd`: 修改时间，TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
- `ldf`: 逻辑删除标记，TIMESTAMP NULL DEFAULT NULL

### rm_base_struct(table_name)
移除表的基础结构字段，与 add_base_struct 相反操作。

### add_tree_struct(table_name)
为表添加树形结构支持字段：
- `rpi`: 父节点 ID，BIGINT DEFAULT NULL
- 自动为 `rpi` 字段创建索引

### rm_tree_struct(table_name)
移除表的树形结构字段，与 add_tree_struct 相反操作。

### add_presort_tree_struct(table_name)
为表添加预排序树结构支持字段：
- `rpi`: 父节点 ID，BIGINT DEFAULT NULL
- `rln`: 左节点编号，BIGINT DEFAULT 1
- `rrn`: 右节点编号，BIGINT DEFAULT 2
- 自动为所有字段创建索引

### rm_presort_tree_struct(table_name)
移除表的预排序树结构字段，与 add_presort_tree_struct 相反操作。

### all_to_nullable(table_name)
将表中所有非主键字段设置为可空：
- 排除主键字段
- 移除字段的 NOT NULL 约束
- 移除字段的默认值

## 使用方式

### 1. 添加依赖

```kotlin
dependencies {
    implementation("io.github.truenine:composeserver-rds-flyway-migration-mysql8:0.0.10")
}
```

### 2. 配置数据源

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
```

### 3. 自动配置

模块会自动配置 Flyway 并执行迁移脚本，无需额外配置。

## 迁移文件

- `V1__create_proc_ct_idx.sql`: 创建索引存储过程
- `V2__create_proc_base_struct.sql`: 创建基础结构管理存储过程
- `V3__create_proc_tree_struct.sql`: 创建树形结构管理存储过程
- `V4__create_proc_presort_tree_struct.sql`: 创建预排序树结构管理存储过程
- `V5__create_proc_all_to_nullable.sql`: 创建字段可空化存储过程

## 测试

模块包含完整的测试套件：
- 基础连接测试
- 存储过程功能测试
- 幂等性测试
- 集成测试

运行测试：
```bash
./gradlew :rds:rds-flyway-migration-mysql8:test
```

## 注意事项

1. **MySQL 版本要求**：需要 MySQL 8.0 或更高版本
2. **字符编码**：建议使用 UTF-8 编码
3. **权限要求**：数据库用户需要有创建表、索引、存储过程的权限
4. **幂等性**：所有存储过程都支持重复执行

## 依赖模块

- `rds-flyway-migration-shared`: 共享的 Flyway 配置
- `testtoolkit`: 测试工具包（仅测试时）
