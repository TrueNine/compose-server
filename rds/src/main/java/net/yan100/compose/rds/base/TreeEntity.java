package net.yan100.compose.rds.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.yan100.compose.core.annotations.BigIntegerAsString;
import net.yan100.compose.core.consts.DataBaseBasicFieldNames;

import java.io.Serial;
import java.io.Serializable;

/**
 * 预排序树
 *
 * @author TrueNine
 * @since 2022-12-12
 */
@Setter
@Getter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "预排序树")
public class TreeEntity extends BaseEntity implements Serializable {
  /**
   * 父id
   */
  public static final String RPI = DataBaseBasicFieldNames.PARENT_ID;
  /**
   * 左节点
   */
  public static final String RLN = DataBaseBasicFieldNames.LEFT_NODE;
  /**
   * 右节点
   */
  public static final String RRN = DataBaseBasicFieldNames.RIGHT_NODE;
  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 父id
   */
  @JsonIgnore
  @Column(name = DataBaseBasicFieldNames.PARENT_ID)
  @Schema(title = "父id")
  private String rpi;

  /**
   * 左节点
   */
  @JsonIgnore
  @BigIntegerAsString
  @Column(name = DataBaseBasicFieldNames.LEFT_NODE)
  @Schema(title = "左节点", hidden = true)
  private Long rln;

  /**
   * 右节点
   */
  @JsonIgnore
  @BigIntegerAsString
  @Column(name = DataBaseBasicFieldNames.RIGHT_NODE)
  @Schema(title = "右节点", hidden = true)
  private Long rrn;

  /**
   * 节点级别
   */
  @JsonIgnore
  @BigIntegerAsString
  @Schema(title = "节点级别", defaultValue = "0")
  @Column(name = DataBaseBasicFieldNames.NODE_LEVEL)
  private Long nlv = 0L;
}
