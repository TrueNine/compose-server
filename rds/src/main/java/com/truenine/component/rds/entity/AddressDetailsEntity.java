package com.truenine.component.rds.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "详细地址")
@Table(name = AddressDetailsEntity.TABLE_NAME)
public class AddressDetailsEntity extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "address_details";
  public static final String ADDRESS_ID = "address_id";
  public static final String ADDRESS_DETAILS = "address_details";
  public static final String CENTER = "center";
  public static final String MAPPED_BY_ADDRESS = "address";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 地址 id
   */
  @Schema(title = "地址 id")
  @Column(name = ADDRESS_ID, nullable = false)
  private String addressId;
  /**
   * 地址
   */
  @ManyToOne
  @Schema(title = "地址")
  @JoinColumn(
    name = ADDRESS_ID,
    referencedColumnName = ID,
    foreignKey = @ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @NotFound(action = IGNORE)
  @JsonBackReference
  private AddressEntity address;

  /**
   * 地址详情
   */
  @Schema(title = "地址详情")
  @Column(name = ADDRESS_DETAILS, nullable = false)
  private String addressDetails;

  /**
   * 定位
   */
  @Nullable
  @Schema(title = "定位")
  @Column(name = CENTER)
  @Convert(converter = PointModelConverter.class)
  private PointModel center;
}
