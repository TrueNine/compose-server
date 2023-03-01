package io.tn.rds.dao;

import io.tn.rds.base.BaseDao;
import io.tn.rds.base.BaseDao;
import org.hibernate.Hibernate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 角色
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
@Schema(title = "角色")
@Table(name = RoleDao.$T_NAME)
public class RoleDao extends BaseDao implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  public static final String $T_NAME = "role";

  public static final String NAME = "name";

  public static final String DOC = "doc";

  /**
   * 角色名称
   */
  @Schema(
      name = NAME,
      description = "角色名称"
  )
  @Column(table = $T_NAME,
      name = NAME)
  @Nullable
  private String name;

  /**
   * 角色描述
   */
  @Schema(
      name = DOC,
      description = "角色描述"
  )
  @Column(table = $T_NAME,
      name = DOC)
  @Nullable
  private String doc;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (RoleDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
