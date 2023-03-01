package io.tn.oss.amazon;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class S3PrincipalArgs {
    @JsonProperty("AWS")
    @SerializedName("AWS")
    private List<String> aws = new ArrayList<>();
}
