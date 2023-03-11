package com.truenine.component.rds.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.truenine.component.core.db.Bf;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
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
@RequiredArgsConstructor
@Schema(title = "预排序树")
public class PreSortTreeDao extends BaseDao {

  /**
   * 父id
   */
  public static final String CPI = Bf.PARENT_ID;

  /**
   * 左节点
   */
  public static final String CLN = Bf.LEFT_NODE;

  /**
   * 右节点
   */
  public static final String CRN = Bf.RIGHT_NODE;

  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = Bf.PARENT_ID)
  @Schema(title = "父id")
  protected String cpi = null;

  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = Bf.LEFT_NODE)
  @Schema(title = "左节点")
  protected Long cln;

  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = Bf.RIGHT_NODE)
  @Schema(title = "右节点")
  protected Long crn;

  @JsonIgnore
  public boolean isLeafNode() {
    return crn - 1 == cln;
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
    PreSortTreeDao that = (PreSortTreeDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return hashCode();
  }
}
