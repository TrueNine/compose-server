package com.truenine.component.rds.models;

import jakarta.annotation.Nullable;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TableRowChangeSerializableObjectModel implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
  private String id;
  private Integer modelHash;
  private String lang;
  private String namespace;
  @Nullable
  private String entityJson;
}
