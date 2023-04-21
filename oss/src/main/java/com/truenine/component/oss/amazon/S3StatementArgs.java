package com.truenine.component.oss.amazon;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class S3StatementArgs {

  @JsonProperty("Effect")
  @SerializedName("Effect")
  private String effect;

  @JsonProperty("Action")
  @SerializedName("Action")
  private List<String> action = new ArrayList<>();

  @JsonProperty("Principal")
  @SerializedName("Principal")
  private S3PrincipalArgs principal;

  @JsonProperty("Resource")
  @SerializedName("Resource")
  private List<String> resource = new ArrayList<>();
}
