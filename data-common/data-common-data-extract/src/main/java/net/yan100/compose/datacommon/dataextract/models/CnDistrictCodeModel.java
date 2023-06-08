package net.yan100.compose.datacommon.dataextract.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
public class CnDistrictCodeModel {
  private final String code;
  private final String provinceCode;
  private final String cityCode;
  private final String countyCode;
  private final String townCode;
  private final String villageCode;

  public CnDistrictCodeModel(String code) {
    Objects.requireNonNull(code);
    var internalCode = Long.parseLong(code);
    if (internalCode >= 100_000_000_000L
      && internalCode <= 1_000_000_000_000L) {
      this.code = code;
      this.provinceCode = code.substring(0, 2);
      this.cityCode = code.substring(2, 4);
      this.countyCode = code.substring(4, 6);
      this.townCode = code.substring(6, 9);
      this.villageCode = code.substring(9, 12);
    } else {
      throw new IllegalArgumentException("行政区编码不满足 12 数值 位要求：code = " + code);
    }
  }

  public Integer getLevel() {
    var zero = "00";
    var threeZero = "000";
    var maxLevel = 5;
    if (villageCode.equals(threeZero))
      maxLevel -= 1;
    if (townCode.equals(threeZero))
      maxLevel -= 1;
    if (countyCode.equals(zero))
      maxLevel -= 1;
    if (cityCode.equals(zero))
      maxLevel -= 1;
    if (provinceCode.equals(zero))
      maxLevel -= 1;
    return maxLevel;
  }
}
