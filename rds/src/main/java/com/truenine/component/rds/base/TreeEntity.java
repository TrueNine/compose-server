package com.truenine.component.rds.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
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
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

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
@Table(indexes = {
  @Index(name = TreeEntity.RLN, columnList = TreeEntity.RLN),
  @Index(name = TreeEntity.RRN, columnList = TreeEntity.RRN),
  @Index(name = TreeEntity.RPI, columnList = TreeEntity.RPI)
})
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
  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = DataBaseBasicFieldNames.PARENT_ID)
  @Schema(title = "父id")
  protected Long rpi = null;

  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = DataBaseBasicFieldNames.LEFT_NODE)
  @Schema(title = "左节点")
  protected Long rln;

  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = DataBaseBasicFieldNames.RIGHT_NODE)
  @Schema(title = "右节点")
  protected Long rrn;

  @JsonIgnore
  public boolean isLeafNode() {
    return rrn - 1 == rln;
  }

  @JsonIgnore
  public void setLeafNode(boolean leafNode) {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    TreeEntity that = (TreeEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }
}
