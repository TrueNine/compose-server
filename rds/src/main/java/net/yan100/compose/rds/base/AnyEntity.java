package net.yan100.compose.rds.base;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.yan100.compose.core.consts.DataBaseBasicFieldNames;
import net.yan100.compose.rds.autoconfig.BizCodeGeneratorBean;
import net.yan100.compose.rds.autoconfig.SnowflakeIdGeneratorBean;
import net.yan100.compose.rds.listener.BizCodeInsertListener;
import net.yan100.compose.rds.listener.PreSaveDeleteReferenceListener;
import net.yan100.compose.rds.listener.TableRowDeletePersistenceListener;
import org.hibernate.Hibernate;
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
  name = BizCodeGeneratorBean.NAME,
  strategy = BizCodeGeneratorBean.CLASS_NAME
)// 业务单号生成器具
@Setter
@Getter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "顶级任意抽象类")
@EntityListeners({
  TableRowDeletePersistenceListener.class,
  BizCodeInsertListener.class,
  PreSaveDeleteReferenceListener.class
})
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
  @GenericGenerator(
    name = SnowflakeIdGeneratorBean.NAME,
    strategy = SnowflakeIdGeneratorBean.CLASS_NAME
  )// 雪花算法生成器
  @Column(name = DataBaseBasicFieldNames.ID)
  @GeneratedValue(generator = SnowflakeIdGeneratorBean.NAME)
  @Schema(title = ID, example = "7001234523405")
  protected String id;

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

  @Override
  public String toString() {
    return String.valueOf(this.getId());
  }
}