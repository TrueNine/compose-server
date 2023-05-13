package net.yan100.compose.datacommon.dataextract.models;

import lombok.Data;

@Data
public class CnDistrictModel {
  private CnDistrictCodeModel codeModel;
  private String name;
  private Integer level;
}
