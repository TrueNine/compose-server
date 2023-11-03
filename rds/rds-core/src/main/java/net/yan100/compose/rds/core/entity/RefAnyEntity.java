package net.yan100.compose.rds.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.yan100.compose.core.consts.DataBaseBasicFieldNames;

import java.io.Serial;
import java.io.Serializable;

/**
 * 任意外键dao
 *
 * @author TrueNine
 * @since 2022-12-12
 */
@Setter
@Getter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "任意外键类型，通常与策略模式一起使用")
public class RefAnyEntity extends BaseEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 任意外键
   */
  public static final String ARI = DataBaseBasicFieldNames.ANY_REFERENCE_ID;

  /**
   * 任意类型
   */
  public static final String TYP = DataBaseBasicFieldNames.ANY_REFERENCE_TYPE;

  @JsonIgnore
  @Column(name = DataBaseBasicFieldNames.ANY_REFERENCE_ID)
  @Schema(title = "任意外键id")
  protected Long ari;

  @JsonIgnore
  @Column(name = DataBaseBasicFieldNames.ANY_REFERENCE_TYPE)
  @Schema(title = "外键类别")
  protected String typ;
}
