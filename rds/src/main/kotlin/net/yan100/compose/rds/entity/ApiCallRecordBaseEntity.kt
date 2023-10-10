package net.yan100.compose.rds.entity

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.annotation.Nullable
import jakarta.persistence.*
import net.yan100.compose.rds.base.BaseEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import java.io.Serial

/**
 * API请求记录
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "API请求记录")
@Table(name = ApiCallRecordBaseEntity.TABLE_NAME)
open class ApiCallRecordBaseEntity : BaseEntity() {
  /**
   * 从属 API
   */
  @Schema(title = "API", requiredMode = RequiredMode.NOT_REQUIRED)
  @ManyToOne
  @JoinColumn(
    name = API_ID, referencedColumnName = ID, foreignKey = ForeignKey(
      ConstraintMode.NO_CONSTRAINT
    )
  )
  @NotFound(action = NotFoundAction.IGNORE)
  open var api: Api? = null

  /**
   * 设备 id, 浏览器为 agent
   */
  @Nullable
  @Schema(title = "设备 id, 浏览器为 agent")
  @Column(name = DEVICE_CODE)
  open var deviceCode: String? = null

  /**
   * 请求 ip
   */
  @Nullable
  @Schema(title = "请求 ip")
  @Column(name = REQ_IP)
  open var reqIp: String? = null

  /**
   * 登录 ip
   */
  @Nullable
  @Schema(title = "登录 ip")
  @Column(name = LOGIN_IP)
  open var loginIp: String? = null

  /**
   * 响应码
   */
  @Nullable
  @Schema(title = "响应码")
  @Column(name = RESP_CODE)
  open var respCode: Int? = null

  /**
   * 请求结果
   */
  @Nullable
  @Schema(title = "请求结果")
  @Column(name = RESP_RESULT_ENC)
  open var respResultEnc: String? = null

  companion object {
    const val TABLE_NAME = "api_call_record"
    const val API_ID = "api_id"
    const val DEVICE_CODE = "device_code"
    const val REQ_IP = "req_ip"
    const val RESP_CODE = "resp_code"
    const val RESP_RESULT_ENC = "resp_result_enc"

    @Serial
    private val serialVersionUID = 1L
    private const val LOGIN_IP = "login_ip"
  }
}
