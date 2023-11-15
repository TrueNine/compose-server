package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.rds.core.entities.TreeEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate


/**
 * 权限
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "权限")
@Table(name = Permissions.TABLE_NAME)
open class Permissions : TreeEntity() {
  /**
   * 权限名
   */
  @Nullable
  @Schema(title = "权限名")
  @Column(name = NAME)
  open var name: String? = null

  /**
   * 权限描述
   */
  @Nullable
  @Schema(title = "权限描述")
  @Column(name = DOC)
  open var doc: String? = null

  companion object {
    const val TABLE_NAME = "permissions"
    const val NAME = "name"
    const val DOC = "doc"
  }
}
