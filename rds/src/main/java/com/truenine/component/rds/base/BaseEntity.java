package com.truenine.component.rds.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.truenine.component.core.consts.DataBaseBasicFieldNames;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;

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
public class BaseEntity extends AnyEntity implements Serializable {


  /**
   * 乐观锁版本
   */
  public static final String RLV = DataBaseBasicFieldNames.LOCK_VERSION;
  /**
   * 逻辑删除标志
   */
  public static final String LDF = DataBaseBasicFieldNames.LOGIC_DELETE_FLAG;
  @Serial
  private static final long serialVersionUID = 1L;

  @Version
  @JsonIgnore
  @Column(name = RLV, nullable = false)
  @Schema(title = "乐观锁版本")
  protected Long rlv;

  @JsonIgnore
  @Column(name = LDF, nullable = false)
  @Schema(title = "逻辑删除标志")
  protected Boolean ldf = false;
}
