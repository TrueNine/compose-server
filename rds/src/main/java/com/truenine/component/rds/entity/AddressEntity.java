package com.truenine.component.rds.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.truenine.component.rds.base.PointModel;
import com.truenine.component.rds.base.TreeEntity;
import com.truenine.component.rds.converters.PointModelConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;

/**
 * 行政区代码
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
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
  @Nullable
  @Schema(title = "代码")
  @Column(name = CODE)
  private String code;

  /**
   * 名称
   */
  @Nullable
  @Schema(title = "名称")
  @Column(name = NAME)
  private String name;

  /**
   * 级别 0 为国家
   */
  @Nullable
  @Schema(title = "级别 0 为国家")
  @Column(name = LEVEL)
  private Integer level;

  /**
   * 定位
   */
  @Nullable
  @Schema(title = "定位")
  @Column(name = CENTER)
  @Convert(converter = PointModelConverter.class)
  private PointModel center;

  /**
   * 当前地址包含的地址详情
   */
  @Schema(title = "包含的地址详情", requiredMode = NOT_REQUIRED)
  @OneToMany(mappedBy = AddressDetailsEntity.MAPPED_BY_ADDRESS, targetEntity = AddressDetailsEntity.class)
  @JsonBackReference
  private List<AddressDetailsEntity> details;
}
