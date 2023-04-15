package com.truenine.component.rds.entity;

import com.truenine.component.rds.base.BaseEntity;
import com.truenine.component.rds.base.PointModel;
import com.truenine.component.rds.converters.PointModelConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;

import java.io.Serial;
import java.io.Serializable;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static org.hibernate.annotations.NotFoundAction.IGNORE;

/**
 * 详细地址
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "详细地址")
@Table(name = AddressDetailsEntity.TABLE_NAME)
public class AddressDetailsEntity extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "address_details";
  public static final String ADDRESS_ID = "address_id";
  public static final String ADDRESS_DETAILS = "address_details";
  public static final String CENTER = "center";
  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 地址
   */
  @Schema(title = "地址")
  @ManyToOne
  @JoinColumn(name = ADDRESS_ID, referencedColumnName = ID, foreignKey = @ForeignKey(NO_CONSTRAINT))
  @NotFound(action = IGNORE)
  private AddressEntity address;

  /**
   * 地址详情
   */
  @Schema(name = ADDRESS_DETAILS, description = "地址详情")
  @Column(table = TABLE_NAME, name = ADDRESS_DETAILS, nullable = false)
  private Long addressDetails;

  /**
   * 定位
   */
  @Schema(name = CENTER, description = "定位")
  @Column(table = TABLE_NAME, name = CENTER)
  @Nullable
  @Convert(converter = PointModelConverter.class)
  private PointModel center;
}
