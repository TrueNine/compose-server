package net.yan100.compose.core.alias

/**
 * 数据库主键
 */
typealias Id = String
/**
 * 字符串形式的序列号
 */
typealias SerialCode = String

/**
 * 数据库外键
 * @see Id
 */
typealias ReferenceId = Id

/**
 * @see ReferenceId
 */
typealias RefId = ReferenceId

/**
 * 大文本
 */
typealias BigText = String

/**
 * 长数字序列号
 */
typealias BigSerial = Long

/**
 * 类型数字
 */
typealias TypeInt = Int

/**
 * 字符串类型序列号
 * @see SerialCode
 */
typealias TypeString = SerialCode

/**
 * @see TypeString
 */
typealias TypeStr = TypeString
