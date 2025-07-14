package io.github.truenine.composeserver

import io.github.truenine.composeserver.domain.IPage
import io.github.truenine.composeserver.domain.IPageParam
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Lowercase string type alias for better readability in domain modeling.
 *
 * Provides a more concise and consistent naming convention across the codebase, especially useful in data classes and API definitions where brevity improves
 * readability.
 *
 * @see String
 */
typealias string = String

/**
 * Lowercase byte type alias for consistent primitive type naming.
 *
 * Used primarily in binary data processing and low-level operations where a unified naming convention with other primitive aliases is desired.
 *
 * @see Byte
 */
typealias byte = Byte

/**
 * Lowercase integer type alias for improved code consistency.
 *
 * Commonly used in mathematical operations, counters, and index calculations where the lowercase naming provides better visual consistency with other type
 * aliases.
 *
 * @see Int
 */
typealias int = Int

/**
 * Lowercase short type alias for consistent primitive type naming.
 *
 * Primarily used in scenarios requiring 16-bit integer values with memory optimization, maintaining naming consistency with other primitive type aliases.
 *
 * @see Short
 */
typealias short = Short

/**
 * Lowercase float type alias for consistent floating-point type naming.
 *
 * Used in mathematical calculations and graphics operations where single-precision floating-point numbers are sufficient and naming consistency is important.
 *
 * @see Float
 */
typealias float = Float

/**
 * Lowercase double type alias for consistent floating-point type naming.
 *
 * Preferred for high-precision mathematical calculations and financial computations where double-precision floating-point accuracy is required.
 *
 * @see Double
 */
typealias double = Double

/**
 * Lowercase long type alias for consistent primitive type naming.
 *
 * Essential for timestamp handling, large number operations, and database primary keys where 64-bit integer precision is required.
 *
 * @see Long
 */
typealias long = Long

/**
 * Decimal type alias for precise financial calculations.
 *
 * Provides arbitrary-precision decimal arithmetic, essential for monetary calculations where floating-point precision errors must be avoided. Commonly used in
 * payment processing, accounting systems, and financial reporting.
 *
 * @see BigDecimal
 */
typealias decimal = BigDecimal

/**
 * Big integer type alias for arbitrary-precision integer arithmetic.
 *
 * Used in cryptographic operations, large number computations, and scenarios where integer values exceed the range of primitive long type.
 *
 * @see BigInteger
 */
typealias bigint = BigInteger

/**
 * Unix timestamp type alias representing milliseconds since epoch.
 *
 * Standardizes timestamp representation across the system as Long values, facilitating consistent time handling in database operations, API responses, and
 * inter-service communication.
 *
 * @see Long
 */
typealias timestamp = Long

/**
 * Date type alias for date-only operations without time components.
 *
 * Represents calendar dates (year-month-day) without timezone or time information, ideal for business logic involving birthdays, deadlines, and scheduling.
 *
 * @see LocalDate
 */
typealias date = LocalDate

/**
 * Time type alias for time-only operations without date components.
 *
 * Represents time of day (hour-minute-second) without date or timezone information, useful for recurring schedules, business hours, and time-based
 * configurations.
 *
 * @see LocalTime
 */
typealias time = LocalTime

/**
 * DateTime type alias for combined date and time operations.
 *
 * Represents both date and time components without timezone information, commonly used in business applications for event scheduling and logging.
 *
 * @see LocalDateTime
 */
typealias datetime = LocalDateTime

/**
 * Instant type alias for precise timestamp representation.
 *
 * Represents a specific moment in time with nanosecond precision, ideal for high-precision timing, event ordering, and distributed system coordination.
 *
 * @see java.time.Instant
 */
typealias instant = java.time.Instant

/**
 * Database primary key type alias.
 *
 * Standardizes primary key representation across all database entities as Long values, ensuring consistent identity handling and supporting large-scale data
 * operations. Used extensively in JPA entities and repository operations.
 *
 * @see Long
 */
typealias Id = Long

/**
 * Reference ID type alias for foreign key relationships.
 *
 * Represents foreign key references to other entities, maintaining type safety and semantic clarity in relational data modeling. Identical to [Id] but provides
 * explicit intent for referential relationships.
 *
 * @see Id
 */
typealias RefId = Id

/**
 * Page Query parameter type alias for pagination requests.
 *
 * Encapsulates pagination parameters (offset, page size, unpaged flag) in a standardized format across all paginated API endpoints. Supports both traditional
 * offset-based and cursor-based pagination patterns.
 *
 * @see IPageParam
 */
typealias Pq = IPageParam

/**
 * Page Result type alias for paginated response data.
 *
 * Standardizes paginated response format containing data collection, total count, and pagination metadata. Provides type-safe generic container for any
 * paginated data type across REST APIs and service layers.
 *
 * @param T The type of data elements in the paginated result
 * @see IPage
 */
typealias Pr<T> = IPage<T>
