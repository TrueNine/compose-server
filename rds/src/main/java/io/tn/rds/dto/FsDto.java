package io.tn.rds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.tn.core.api.http.MediaTypes;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 文件入参
 *
 * @author TrueNine
 * @since 2022-12-31
 */
@Data
@Schema(title = "文件参数")
public class FsDto {

  @NotNull
  @NotBlank
  @Schema(name = "fullName", description = "文件全名")
  private String fullName;

  @NotNull
  @NotBlank
  @Schema(name = "mimeType", description = "文件 MIME")
  private String mimeType = MediaTypes.BINARY.media();

  @NotNull
  @NotBlank
  @Schema(name = "saveName", description = "保存文件名称")
  private String saveName = fullName;

  @Schema(name = "doc", description = "文件描述")
  private String doc;

  @NotNull
  @NotBlank
  @Schema(name = "url", description = "url")
  private String url;

  @NotNull
  @NotBlank
  @Schema(name = "dir", description = "文件夹", defaultValue = "/")
  private String dir = "/";

  @Schema(name = "rnType", description = "是否为远程存储")
  private Boolean rnType = true;


  @Min(1)
  @NotNull
  @Schema(name = "byteSize", description = "文件大小", defaultValue = "0L")
  private Long byteSize = 0L;
}
