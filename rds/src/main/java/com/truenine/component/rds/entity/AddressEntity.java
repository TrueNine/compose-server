package com.truenine.component.rds.entity;

import com.truenine.component.rds.base.PresortTreeEntity;
import com.truenine.component.rds.converters.PointModelConverter;
import com.truenine.component.rds.models.PointModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
@Table(name = AddressEntity.TABLE_NAME)
public class AddressEntity extends PresortTreeEntity implements Serializable {

  public static final String TABLE_NAME = "address";
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
    title = CODE,
    description = "代码"
  )
  @Column(table = TABLE_NAME,
    name = CODE)
  @Nullable
  private String code;

  /**
   * 名称
   */
  @Schema(
    title = NAME,
    description = "名称"
  )
  @Column(table = TABLE_NAME,
    name = NAME)
  @Nullable
  private String name;

  /**
   * 级别 0 为国家
   */
  @Schema(
    title = LEVEL,
    description = "级别 0 为国家"
  )
  @Column(table = TABLE_NAME,
    name = LEVEL)
  @Nullable
  private Integer level;

  /**
   * 定位
   */
  @Schema(
    title = CENTER,
    description = "定位"
  )
  @Column(table = TABLE_NAME,
    name = CENTER)
  @Nullable
  @Convert(converter = PointModelConverter.class)
  private PointModel center;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (AddressEntity) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
