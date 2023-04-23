package com.truenine.component.rds.base;

import com.truenine.component.core.consts.DataBaseBasicFieldNames;
import com.truenine.component.rds.autoconfig.BizCodeGeneratorBean;
import com.truenine.component.rds.autoconfig.SnowflakeIdGeneratorBean;
import com.truenine.component.rds.listener.TableRowDeletePersistenceListener;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * JPA的最基础基类，包括一个 id
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@GenericGenerator(
  name = SnowflakeIdGeneratorBean.NAME,
  strategy = SnowflakeIdGeneratorBean.CLASS_NAME
)// 雪花算法生成器
@GenericGenerator(
  name = BizCodeGeneratorBean.NAME,
  strategy = BizCodeGeneratorBean.CLASS_NAME
)// 业务单号生成器具
@Setter
@Getter
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

  /**
   * id
   */
  @Id
  @Column(name = DataBaseBasicFieldNames.ID)
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
