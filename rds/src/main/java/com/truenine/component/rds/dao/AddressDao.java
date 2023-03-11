package com.truenine.component.rds.dao;

import com.truenine.component.rds.base.PreSortTreeDao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.geo.Point;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * 行政区代码
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@ToString
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "行政区代码")
@Table(name = AddressDao.$T_NAME)
public class AddressDao extends PreSortTreeDao implements Serializable {

  public static final String $T_NAME = "address";
  public static final String CODE = "code";
  public static final String NAME = "name";
  public static final String LEVEL = "level";
  public static final String CENTER = "center";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 代码
   */
  @Schema(
    name = CODE,
    description = "代码"
  )
  @Column(table = $T_NAME,
    name = CODE)
  @Nullable
  private String code;

  /**
   * 名称
   */
  @Schema(
    name = NAME,
    description = "名称"
  )
  @Column(table = $T_NAME,
    name = NAME)
  @Nullable
  private String name;

  /**
   * 级别 0 为国家
   */
  @Schema(
    name = LEVEL,
    description = "级别 0 为国家"
  )
  @Column(table = $T_NAME,
    name = LEVEL)
  @Nullable
  private Integer level;

  /**
   * 定位
   */
  @Schema(
    name = CENTER,
    description = "定位"
  )
  @Column(table = $T_NAME,
    name = CENTER)
  @Nullable
  private Point center;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (AddressDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
