# Jackson configuration management module

This module provides centralized Jackson configuration management, including a configuration properties class and an ObjectMapper holder.

## JacksonProperties configuration class

`JacksonProperties` is a Spring Boot configuration properties class exposing core Jackson options:

### Properties

- `compose.jackson.enableTimestampSerialization`: Whether to enable timestamp serialization (default: true)
- `compose.jackson.timestampUnit`: Timestamp unit, `MILLISECONDS` or `SECONDS` (default: `MILLISECONDS`)
- `compose.jackson.serializationInclusion`: Serialization inclusion strategy (default: `NON_NULL`)
- `compose.jackson.failOnUnknownProperties`: Whether to fail on unknown properties (default: false)
- `compose.jackson.writeDatesAsTimestamps`: Whether to write dates as timestamps (default: true)

### Example

```yaml
compose:
  jackson:
    enableTimestampSerialization: true
    timestampUnit: MILLISECONDS
    serializationInclusion: NON_NULL
    failOnUnknownProperties: false
    writeDatesAsTimestamps: true
```

## ObjectMapperHolder configuration holder

`ObjectMapperHolder` is a Spring component that provides unified access to different `ObjectMapper` instances:

### Key methods

- `getDefaultMapper()`: Get the default configured ObjectMapper
- `getNonIgnoreMapper()`: Get the ObjectMapper that does not ignore unknown properties
- `getMapper(ignoreUnknown: Boolean = true)`: Get an ObjectMapper according to the `ignoreUnknown` flag

### Usage example

```kotlin
@Resource
private lateinit var objectMapperHolder: ObjectMapperHolder

fun example() {
    // Get default mapper (ignores unknown properties)
    val defaultMapper = objectMapperHolder.getDefaultMapper()

    // Get non-ignore mapper (does not ignore unknown properties)
    val nonIgnoreMapper = objectMapperHolder.getNonIgnoreMapper()

    // Choose mapper as needed
    val mapper = objectMapperHolder.getMapper(ignoreUnknown = false)
}
```

## Bean naming convention

- `defaultObjectMapper`: Default ObjectMapper bean
- `nonIgnoreObjectMapper`: Non-ignore ObjectMapper bean

These bean names are defined in `JacksonAutoConfiguration` to ensure consistent naming.
