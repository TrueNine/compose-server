package com.truenine.component.rds.models.req;

import com.truenine.component.core.http.MediaTypes;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(title = "附件添加入参")
public class PutAttachmentRequestParam {

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
  @Schema(name = "size", description = "文件大小", defaultValue = "0L")
  private Long size = 0L;
}