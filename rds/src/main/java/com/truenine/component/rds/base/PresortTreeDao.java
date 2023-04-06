package com.truenine.component.rds.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.truenine.component.core.consts.Bf;
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
  @Index(name = PresortTreeDao.RLN, columnList = PresortTreeDao.RLN),
  @Index(name = PresortTreeDao.RRN, columnList = PresortTreeDao.RRN),
  @Index(name = PresortTreeDao.RPI, columnList = PresortTreeDao.RPI)
})
@RequiredArgsConstructor
@Schema(title = "预排序树")
public class PresortTreeDao extends BaseDao {

  /**
   * 父id
   */
  public static final String RPI = Bf.PARENT_ID;

  /**
   * 左节点
   */
  public static final String RLN = Bf.LEFT_NODE;

  /**
   * 右节点
   */
  public static final String RRN = Bf.RIGHT_NODE;

  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = Bf.PARENT_ID)
  @Schema(title = "父id")
  protected String rpi = null;

  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = Bf.LEFT_NODE)
  @Schema(title = "左节点")
  protected Long rln;

  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = Bf.RIGHT_NODE)
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
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    PresortTreeDao that = (PresortTreeDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return hashCode();
  }
}
