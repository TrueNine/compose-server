package com.truenine.component.rds.entity;

import com.truenine.component.rds.base.PointModel;
import com.truenine.component.rds.base.TreeEntity;
import com.truenine.component.rds.converters.PointModelConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;

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
public class AddressEntity extends TreeEntity implements Serializable {
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
  @Schema(title = "代码")
  @Column(name = CODE)
  @Nullable
  private String code;

  /**
   * 名称
   */
  @Schema(title = "名称")
  @Column(name = NAME)
  @Nullable
  private String name;

  /**
   * 级别 0 为国家
   */
  @Schema(title = "级别 0 为国家")
  @Column(name = LEVEL)
  @Nullable
  private Integer level;

  /**
   * 定位
   */
  @Schema(title = "定位")
  @Column(name = CENTER)
  @Nullable
  @Convert(converter = PointModelConverter.class)
  private PointModel center;
}
