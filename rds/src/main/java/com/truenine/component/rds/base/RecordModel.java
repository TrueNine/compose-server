package com.truenine.component.rds.base;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(title = "表行对象序列化模型")
public class RecordModel implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
  private String id;
  private Integer modelHash;
  private String lang;
  private String namespace;
  @Nullable
  private String entityJson;
}
