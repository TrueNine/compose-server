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
import java.util.Objects;

/**
 * 用户组
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
@Schema(title = "用户组")
@Table(name = UserGroupDao.$T_NAME, indexes = {
  @Index(name = "user_id_idx", columnList = "user_id"),
})
public class UserGroupDao extends BaseDao implements Serializable {

  public static final String $T_NAME = "user_group";
  public static final String USER_ID = "user_id";
  public static final String NAME = "name";
  public static final String DOC = "doc";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 创建人
   */
  @Schema(
    name = USER_ID,
    description = "创建人"
  )
  @Column(table = $T_NAME,
    name = USER_ID)
  @Nullable
  private String userId;

  /**
   * 名称
   */
  @Schema(
    name = NAME,
    description = "名称"
  )
  @Column(table = $T_NAME,
    name = NAME)
  @Nullable
  private String name;

  /**
   * 描述
   */
  @Schema(
    name = DOC,
    description = "描述"
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
    var that = (UserGroupDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
