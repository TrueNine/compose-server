package com.truenine.component.rds.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

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
public class PointModel implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
  private Double x;
  private Double y;
}
