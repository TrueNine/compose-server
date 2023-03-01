package io.tn.rds.dao;

import io.swagger.v3.oas.annotations.media.Schema;
import io.tn.rds.base.BaseDao;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 数据删除备份表
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@ToString
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "数据删除备份表")
@Table(name = DeleteBackupDao.$T_NAME, indexes = {
  @Index(name = "del_datetime_idx", columnList = "del_datetime"),
})
public class DeleteBackupDao extends BaseDao implements Serializable {

  public static final String $T_NAME = "delete_backup";
  public static final String LANG = "lang";
  public static final String NAMESPACES = "namespaces";
  public static final String DEL_SER_OBJ = "del_ser_obj";
  public static final String DEL_DATETIME = "del_datetime";
  public static final String DEL_SYS_VERSION = "del_sys_version";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 编程语言
   */
  @Schema(
    name = LANG,
    description = "编程语言",
    defaultValue = "jvm"
  )
  @Column(table = $T_NAME,
    name = LANG)
  @Nullable
  private String lang = "jvm";

  /**
   * 命名空间,例如 java 的 class, csharp 的 namespace
   */
  @Schema(
    name = NAMESPACES,
    description = "命名空间,例如 java 的 class, csharp 的 namespace"
  )
  @Column(table = $T_NAME,
    name = NAMESPACES)
  @Nullable
  private String namespaces;

  /**
   * 删除数据
   */
  @Schema(
    name = DEL_SER_OBJ,
    description = "删除数据"
  )
  @Column(table = $T_NAME,
    name = DEL_SER_OBJ)
  @Nullable
  private String delSerObj;

  /**
   * 删除日期
   */
  @Schema(
    name = DEL_DATETIME,
    description = "删除日期"
  )
  @Column(table = $T_NAME,
    name = DEL_DATETIME)
  @Nullable
  private LocalDateTime delDatetime = LocalDateTime.now();

  /**
   * 系统版本
   */
  @Schema(
    name = DEL_SYS_VERSION,
    description = "系统版本"
  )
  @Column(table = $T_NAME,
    name = DEL_SYS_VERSION)
  @Nullable
  private String delSysVersion;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (DeleteBackupDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
