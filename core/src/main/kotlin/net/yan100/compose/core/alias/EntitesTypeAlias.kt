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
package net.yan100.compose.core.alias

/** 数据库主键 */
typealias Id = String
/** 字符串形式的序列号 */
typealias SerialCode = String

/**
 * 数据库外键
 *
 * @see Id
 */
typealias ReferenceId = Id

/** @see ReferenceId */
typealias RefId = ReferenceId

/** 大文本 */
typealias BigText = String

/** 长数字序列号 */
typealias BigSerial = Long

/** 类型数字 */
typealias TypeInt = Int

/**
 * 字符串类型序列号
 *
 * @see SerialCode
 */
typealias TypeString = SerialCode

/** @see TypeString */
typealias TypeStr = TypeString
