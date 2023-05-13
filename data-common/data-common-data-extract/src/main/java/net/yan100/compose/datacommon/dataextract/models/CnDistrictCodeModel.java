package net.yan100.compose.datacommon.dataextract.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
public class CnDistrictCodeModel {
  private final Long code;
  private final Integer provinceCode;
  private final Integer cityCode;
  private final Integer countyCode;
  private final Integer townCode;
  private final Integer villageCode;

  public CnDistrictCodeModel(Long code) {
    if (Objects.requireNonNull(code) >= 100_000_000_000L
      && code <= 1_000_000_000_000L) {
      var codeStr = code.toString();
      this.code = code;
      this.provinceCode = Integer.valueOf(codeStr.substring(0, 2));
      this.cityCode = Integer.valueOf(codeStr.substring(2, 4));
      this.countyCode = Integer.valueOf(codeStr.substring(4, 6));
      this.townCode = Integer.valueOf(codeStr.substring(6, 9));
      this.villageCode = Integer.valueOf(codeStr.substring(9, 12));
    } else {
      throw new IllegalArgumentException("行政区编码不满足12位要求：code = " + code);
    }
  }

  public Integer getLevel() {
    var maxLevel = 5;
    if (villageCode == 0)
      maxLevel -= 1;
    if (townCode == 0)
      maxLevel -= 1;
    if (countyCode == 0)
      maxLevel -= 1;
    if (cityCode == 0)
      maxLevel -= 1;
    if (provinceCode == 0)
      maxLevel -= 1;
    return maxLevel;
  }

  public void setLevel() {
  }
}
