package com.truenine.component.rds.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.truenine.component.core.consts.Bf;
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

import java.util.Objects;

/**
 * jpa顶级抽象类
 * <p>
 * \@CreateBy
 * \@LastModifyBy
 *
 * @author TrueNine
 * @since 2022-12-12
 */
@Setter
@Getter
@ToString
@DynamicInsert
@DynamicUpdate
@MappedSuperclass
@RequiredArgsConstructor
@Schema(title = "顶级抽象类")
@EntityListeners(TableRowDeletePersistenceListener.class)
public class BaseDao {


  /**
   * 主键
   */
  public static final String ID = Bf.ID;

  /**
   * 乐观锁版本
   */
  public static final String RLV = Bf.LOCK_VERSION;

  /**
   * 逻辑删除标志
   */
  public static final String LDF = Bf.LOGIC_DELETE_FLAG;

  @Id
  @JsonIgnore
  @Column(name = Bf.ID, columnDefinition = "BIGINT UNSIGNED")
  @Expose(deserialize = false)
  @GenericGenerator(
    name = SnowflakeIdGeneratorBean.NAME,
    strategy = SnowflakeIdGeneratorBean.CLASS_NAME
  )
  @GeneratedValue(generator = SnowflakeIdGeneratorBean.NAME)
  @Schema(name = ID,
    description = "主键id",
    defaultValue = "主键自动生成",
    example = "7001234523405")
  protected String id;

  @Version
  @JsonIgnore
  @Column(name = RLV,
    nullable = false)
  @Expose(deserialize = false)
  @Schema(title = "乐观锁版本")
  protected Long rlv;

  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = LDF,
    nullable = false
  )
  @Schema(title = "逻辑删除标志")
  protected Boolean ldf = false;

  // TODO 改写 equals
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (BaseDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
