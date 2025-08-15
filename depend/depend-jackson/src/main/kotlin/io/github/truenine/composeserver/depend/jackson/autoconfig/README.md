# Jackson配置管理组件

本模块提供了统一的Jackson配置管理功能，包括配置属性类和ObjectMapper持有者。

## JacksonProperties配置类

`JacksonProperties`是一个Spring Boot配置属性类，提供了Jackson的核心配置选项：

### 配置属性

- `compose.jackson.enableTimestampSerialization`: 是否启用时间戳序列化（默认：true）
- `compose.jackson.timestampUnit`: 时间戳单位，MILLISECONDS或SECONDS（默认：MILLISECONDS）
- `compose.jackson.serializationInclusion`: 序列化包含策略（默认：NON_NULL）
- `compose.jackson.failOnUnknownProperties`: 遇到未知属性时是否失败（默认：false）
- `compose.jackson.writeDatesAsTimestamps`: 是否将日期写为时间戳（默认：true）

### 使用示例

```yaml
compose:
  jackson:
    enableTimestampSerialization: true
    timestampUnit: MILLISECONDS
    serializationInclusion: NON_NULL
    failOnUnknownProperties: false
    writeDatesAsTimestamps: true
```

## ObjectMapperHolder配置持有者

`ObjectMapperHolder`是一个Spring组件，提供统一的ObjectMapper访问接口：

### 主要方法

- `getDefaultMapper()`: 获取默认配置的ObjectMapper
- `getNonIgnoreMapper()`: 获取非忽略配置的ObjectMapper
- `getMapper(ignoreUnknown: Boolean = true)`: 根据参数获取相应配置的ObjectMapper

### 使用示例

```kotlin
@Resource
private lateinit var objectMapperHolder: ObjectMapperHolder

fun example() {
    // 获取默认mapper（忽略未知属性）
    val defaultMapper = objectMapperHolder.getDefaultMapper()
    
    // 获取非忽略mapper（不忽略未知属性）
    val nonIgnoreMapper = objectMapperHolder.getNonIgnoreMapper()
    
    // 根据需要选择mapper
    val mapper = objectMapperHolder.getMapper(ignoreUnknown = false)
}
```

## Bean命名规范

- `defaultObjectMapper`: 默认ObjectMapper Bean
- `nonIgnoreObjectMapper`: 非忽略ObjectMapper Bean

这些Bean名称在`JacksonAutoConfiguration`中定义，确保了命名的一致性。
