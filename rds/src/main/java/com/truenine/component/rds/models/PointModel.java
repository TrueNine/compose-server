package com.truenine.component.rds.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 地址定位模型
 *
 * @author T_teng
 * @since 2023-04-06
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "位置坐标")
public class PointModel implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @Nullable
  private BigDecimal x;

  @Nullable
  private BigDecimal y;
}
