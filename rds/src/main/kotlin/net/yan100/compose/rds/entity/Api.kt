package net.yan100.compose.rds.entity

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.annotation.Nullable
import jakarta.persistence.*
import lombok.Getter
import lombok.Setter
import net.yan100.compose.rds.base.BaseEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import java.io.Serial
import java.io.Serializable

/**
 * api
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "api")
@Table(name = Api.TABLE_NAME)
open class Api : BaseEntity() {
  /**
   * 名称
   */
  @Schema(title = "名称")
  @Column(name = NAME)
  @Nullable
  open var name: String? = null

  /**
   * 描述
   */
  @Schema(title = "描述")
  @Column(name = DOC)
  @Nullable
  open var doc: String? = null

  /**
   * 权限
   */
  @Nullable
  @Schema(title = "权限", requiredMode = RequiredMode.NOT_REQUIRED)
  @ManyToOne
  @JoinColumn(
    name = PERMISSIONS_ID, referencedColumnName = ID, foreignKey = ForeignKey(
      ConstraintMode.NO_CONSTRAINT
    )
  )
  @NotFound(action = NotFoundAction.IGNORE)
  open var permissions: Permissions? = null

  /**
   * 路径
   */
  @Schema(title = "路径")
  @Column(name = API_PATH)
  @Nullable
  open var apiPath: String? = null

  /**
   * 请求方式
   */
  @Schema(title = "请求方式")
  @Column(name = API_METHOD)
  @Nullable
  open var apiMethod: String? = null

  /**
   * 请求协议
   */
  @Schema(title = "请求协议")
  @Column(name = API_PROTOCOL)
  @Nullable
  open var apiProtocol: String? = null

  companion object {
    const val TABLE_NAME = "api"
    const val NAME = "name"
    const val DOC = "doc"
    const val PERMISSIONS_ID = "permissions_id"
    const val API_PATH = "api_path"
    const val API_METHOD = "api_method"
    const val API_PROTOCOL = "api_protocol"

    @Serial
    private val serialVersionUID = 1L
  }
}
