package io.github.truenine.composeserver

import io.github.truenine.composeserver.domain.IPage
import io.github.truenine.composeserver.domain.IPageParam
import io.github.truenine.composeserver.typing.ISO4217
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

typealias string = String

typealias byte = Byte

typealias int = Int

typealias short = Short

typealias float = Float

typealias double = Double

typealias long = Long

typealias decimal = BigDecimal

typealias bigint = BigInteger

typealias Timestamp = Long

typealias timestamp = Timestamp

typealias date = LocalDate

typealias time = LocalTime

typealias datetime = LocalDateTime

/** 数据库主键 */
typealias Id = Long

/** @see Id */
typealias RefId = Id

typealias Pq = IPageParam

typealias Pr<T> = IPage<T>

typealias ISO4217Typing = ISO4217

typealias Currency = ISO4217
