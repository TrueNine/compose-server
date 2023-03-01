package com.truenine.component.rds.dao;

import com.truenine.component.rds.base.BaseDao;
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
 * 详细地址
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
@Schema(title = "详细地址")
@Table(name = AddressDetailsDao.$T_NAME)
public class AddressDetailsDao extends BaseDao implements Serializable {

  public static final String $T_NAME = "address_details";
  public static final String ADDRESS_ID = "address_id";
  public static final String ADDRESS_DETAILS = "address_details";
  public static final String LOCATION = "location";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 地址
   */
  @Schema(
    name = ADDRESS_ID,
    description = "地址"
  )
  @Column(table = $T_NAME,
    name = ADDRESS_ID,
    nullable = false)
  private Long addressId;

  /**
   * 地址详情
   */
  @Schema(
    name = ADDRESS_DETAILS,
    description = "地址详情"
  )
  @Column(table = $T_NAME,
    name = ADDRESS_DETAILS,
    nullable = false)
  private Long addressDetails;

  /**
   * 定位
   */
  @Schema(
    name = LOCATION,
    description = "定位"
  )
  @Column(table = $T_NAME,
    name = LOCATION)
  @Nullable
  private Point location;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (AddressDetailsDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
