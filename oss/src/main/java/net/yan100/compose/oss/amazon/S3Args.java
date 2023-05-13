package net.yan100.compose.oss.amazon;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class S3Args {
  @JsonProperty("Version")
  @SerializedName("Version")
  String version;

  @JsonProperty("Statement")
  @SerializedName("Statement")
  List<S3StatementArgs> statement = new ArrayList<>();
}
