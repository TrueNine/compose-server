package net.yan100.compose.rds.crud.entities.jpa

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Transient
import net.yan100.compose.datetime
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.meta.annotations.MetaSkipGeneration
import net.yan100.compose.rds.entities.IJpaEntity

/**
 * API请求记录
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@MetaDef
interface SuperApiCallRecord : IJpaEntity {
  /** 设备 id, 浏览器为 agent */
  @get:Schema(title = "设备 id", description = "浏览器为 ua ，其他自定义唯一标识即可")
  var deviceCode: String?

  /** 请求 ip */
  @get:Schema(title = "请求 ip") var reqIp: String?

  /** 登录 ip */
  @get:Schema(title = "登录 ip") var loginIp: String?

  /** 响应码 */
  @get:Schema(title = "响应码") var respCode: Int?

  /** 请求结果 */
  @get:Schema(title = "请求结果") var respResultEnc: String?

  @get:Schema(title = "请求路径") var reqPath: String?

  @get:Schema(title = "请求方法") var reqMethod: String?

  @get:Schema(title = "请求协议") var reqProtocol: String?

  @get:Schema(title = "请求时间") var reqDatetime: datetime?

  @get:Schema(title = "响应时间") var respDatetime: datetime?

  @MetaSkipGeneration
  @get:Transient
  val uriDeep: Int
    get() = reqPath?.split("/")?.filter { it.isNotBlank() }?.size ?: 0
}
