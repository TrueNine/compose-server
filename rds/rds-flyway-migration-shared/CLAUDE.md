# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this Module.

## Overview

The `flyway-migration-shared` module defines common specifications and implementation requirements for database migration in the framework. This module provides unified implementation standards for database structure management functions/stored procedures across different database systems (PostgreSQL, MySQL, Oracle, SQL Server, etc.).

## `ct_idx` Function/Stored Procedure

Creates an index for a specified column in a specified table (if the column exists and the index does not exist). Supports idempotent operations and can be called repeatedly without errors.

**Parameters:**

- `tab_name`: Table name (varchar(128))
- `col_name`: Column name (varchar(255))

**Implementation Logic:**

1. Check if the specified column exists in the specified table
2. If the column exists, check if a corresponding index already exists
3. Create the index only when the column exists and the index does not exist
4. Index naming convention: `{col_name}_idx`

**Core SQL Operations:**

- Column existence check: `select column_name from information_schema.columns where table_name = ? and column_name = ?`
- Index existence check: `select index_name from information_schema.statistics where table_name = ? and index_name = ?`
- Index creation: `create index {col_name}_idx on {tab_name} ({col_name})`

## `add_base_struct` Function/Stored Procedure

**Function:** Add Compose Server standard base fields to a table

**Parameters:**

- `tab_name`: Table name (VARCHAR(128))

**Standard Fields:**

- `id`: Primary key field (BIGINT NOT NULL PRIMARY KEY), default value is non-auto-increment int64 type, **must not set any auto-increment strategy, should be handled by the application**
- `rlv`: Row Lock Version (INT DEFAULT 0), default value is 0 of int32 type, cannot be NULL
- `crd`: Created Row Datetime (TIMESTAMP DEFAULT CURRENT_TIMESTAMP), timezone-independent millisecond timestamp, default value is the timestamp when the table record is inserted
- `mrd`: Modify Row Datetime (TIMESTAMP DEFAULT null), timezone-independent millisecond timestamp, default is null
- `ldf`: Logic Delete Flag (TIMESTAMP NULL DEFAULT NULL)

**Implementation Requirements:**

- Check if fields already exist to avoid duplicate additions
- For tables with existing data, properly handle the addition of the ID field
  - PostgreSQL: Use temporary sequence to generate IDs for existing data
  - MySQL: Use AUTO_INCREMENT feature
- Support idempotent operations

## `rm_base_struct` Function/Stored Procedure

**Function:** Remove base structure fields from a table

**Parameters:**

- `tab_name`: Table name (VARCHAR(128))

**Implementation Requirements:**

- Remove fields in the correct order (remove constraints first, then remove fields)
- Safely handle primary key constraint deletion
- Support idempotent operations

## `add_tree_struct` Function/Stored Procedure

**Function:** Add tree structure support fields to a table

**Parameters:**

- `tab_name`: Table name (VARCHAR(128))

**Tree Structure Fields:**

- `rpi`: Row Parent Id (BIGINT DEFAULT NULL)

**Implementation Requirements:**

- Create index for `rpi` field
- Support idempotent operations

## `add_presort_tree_struct` Function/Stored Procedure

**Function:** Add pre-sorted tree structure support fields to a table

**Parameters:**

- `tab_name`: Table name (VARCHAR(128))

**Pre-sorted Tree Fields:**

- `rpi`: Row Parent Id (BIGINT DEFAULT NULL)
- `rln`: Row Left Node (BIGINT DEFAULT 1)
- `rrn`: Row Right Node (BIGINT DEFAULT 2)
- `nlv`: Node Level (INT DEFAULT 0)
- `tgi`: Tree Group Id (VARCHAR(255))

**Implementation Requirements:**

- Create corresponding indexes for all fields
- Support nested set model tree structure
- Support idempotent operations

## `all_to_nullable` Function/Stored Procedure

**Function:** Set all non-primary key fields in a table to nullable

**Parameters:**

- `tab_name`: Table name (VARCHAR(128))

**Implementation Requirements:**

- Exclude primary key fields
- Remove NOT NULL constraints from fields
- Remove default values from fields
- Support idempotent operations

## Implementation Specifications

### General Requirements

1. **Idempotency**: All functions/stored procedures must support repeated execution without errors
2. **Security**: Use parameterized queries to prevent SQL injection
3. **Error Handling**: Properly handle exceptional situations and provide meaningful error messages
4. **Performance**: Check if objects exist before operations to avoid unnecessary operations
5. **Version Inclusion**: add functions and rm functions should appear in the same version .sql file

### Database-Specific Requirements

#### PostgreSQL

- Use PL/pgSQL language
- Use `format()` function for dynamic SQL construction
- Use `%I` identifier escaping to prevent injection
- Support array operations and loop structures

#### Postgresql

**Function:** Provide implicit conversions between common data types

**Supported Conversions:**

- json ↔ varchar
- bigint ↔ varchar

**Implementation Requirements:**

- Use `with inout as implicit` syntax
- Delete existing conversions first, then create new ones

#### MySQL

- Use stored procedure syntax
- Use `delimiter` to handle statement separators
- Use prepared statements to execute dynamic SQL
- Use `information_schema` for metadata queries

### Naming Conventions

- Function/stored procedure names use lowercase letters separated by underscores
- Index naming: `{column_name}_idx`
- Constraint naming follows database default rules
- Temporary objects use `temp_` prefix
