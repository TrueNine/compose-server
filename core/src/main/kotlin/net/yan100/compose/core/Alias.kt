/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.core

import net.yan100.compose.core.domain.IPage
import net.yan100.compose.core.domain.IPageParam
import net.yan100.compose.core.domain.IPageParamLike
import net.yan100.compose.core.typing.ISO4217
import org.slf4j.Logger
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
typealias PqLike = IPageParamLike
typealias Pr<T> = IPage<T>

typealias ISO4217Typing = ISO4217
typealias Currency = ISO4217

// === rust alias ===
typealias bool = Boolean
typealias i8 = Byte
typealias i16 = Short
typealias i32 = Int
typealias i64 = Long
typealias u8 = UByte
typealias u16 = UShort
typealias u32 = UInt
typealias u64 = ULong
typealias f32 = Float
typealias f64 = Double
typealias char = Char


typealias SysLogger = Logger
