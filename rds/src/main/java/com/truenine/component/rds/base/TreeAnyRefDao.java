package com.truenine.component.rds.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.truenine.component.core.db.Bf;
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
 * 带外键的 预排序树
 *
 * @author TrueNine
 * @since 2022-12-15
 */
@Setter
@Getter
@ToString
@DynamicInsert
@DynamicUpdate
@MappedSuperclass
@Table(indexes = {
  @Index(name = PreSortTreeDao.RLN, columnList = PreSortTreeDao.RLN),
  @Index(name = PreSortTreeDao.RRN, columnList = PreSortTreeDao.RRN),
  @Index(name = PreSortTreeDao.RPI, columnList = PreSortTreeDao.RPI),
  @Index(name = RefAnyDao.ARI, columnList = RefAnyDao.ARI)
})
@RequiredArgsConstructor
@Schema(title = "预排序树和任意外键的结合体")
public class TreeAnyRefDao extends PreSortTreeDao {

  /**
   * 任意外键
   */
  public static final String ARI = Bf.ANY_REFERENCE_ID;

  /**
   * 任意类型
   */
  public static final String TYP = Bf.ANY_REFERENCE_TYPE;

  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = Bf.ANY_REFERENCE_ID)
  @Schema(title = "任意外键id")
  protected String ari;

  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = Bf.ANY_REFERENCE_TYPE)
  @Schema(title = "外键类别")
  protected String typ;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    TreeAnyRefDao that = (TreeAnyRefDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
