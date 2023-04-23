package com.truenine.component.rds.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.truenine.component.core.consts.DataBaseBasicFieldNames;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Index;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
@ToString
@DynamicInsert
@DynamicUpdate
@MappedSuperclass
@Table
@RequiredArgsConstructor
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
  private Long rpi;

  /**
   * 左节点
   */
  @JsonIgnore
  @Column(name = DataBaseBasicFieldNames.LEFT_NODE)
  @Schema(title = "左节点", hidden = true)
  private Long rln;

  /**
   * 右节点
   */
  @JsonIgnore
  @Column(name = DataBaseBasicFieldNames.RIGHT_NODE)
  @Schema(title = "右节点", hidden = true)
  private Long rrn;
}
