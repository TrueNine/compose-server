package io.tn.rds.dao;

import io.tn.rds.base.BaseDao;
import io.tn.rds.base.BaseDao;
import jakarta.persistence.Index;
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
 * 排序
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
@Schema(title = "排序")
@Table(name = TabOrderDao.$T_NAME, indexes = {
  @Index(name = "ordered_idx", columnList = "ordered"),
})
public class TabOrderDao extends BaseDao implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  public static final String $T_NAME = "tab_order";

  public static final String NAME = "name";

  public static final String DOC = "doc";

  public static final String ORDERED = "ordered";

  /**
   * 名称
   */
  @Schema(
    name = NAME,
    description = "名称"
  )
  @Column(table = $T_NAME,
    name = NAME,
    nullable = false)
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

  /**
   * 排序值
   */
  @Schema(
    name = ORDERED,
    description = "排序值"
  )
  @Column(table = $T_NAME,
    name = ORDERED,
    nullable = false)
  private Long ordered;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (TabOrderDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
