package com.truenine.component.rds.base;

import com.truenine.component.core.consts.DataBaseBasicFieldNames;
import com.truenine.component.rds.autoconfig.SnowflakeIdGeneratorBean;
import com.truenine.component.rds.listener.TableRowDeletePersistenceListener;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@ToString
@DynamicInsert
@DynamicUpdate
@MappedSuperclass
@RequiredArgsConstructor
@Schema(title = "顶级任意抽象类")
@EntityListeners(TableRowDeletePersistenceListener.class)
public class AnyEntity implements Serializable {
  /**
   * 主键
   */
  public static final String ID = DataBaseBasicFieldNames.ID;
  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = DataBaseBasicFieldNames.ID)
  @GenericGenerator(
    name = SnowflakeIdGeneratorBean.NAME,
    strategy = SnowflakeIdGeneratorBean.CLASS_NAME
  )
  @GeneratedValue(generator = SnowflakeIdGeneratorBean.NAME)
  @Schema(title = ID, example = "7001234523405")
  protected Long id;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    AnyEntity that = (AnyEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
