package com.truenine.component.rds.models.request;

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
  @Schema(title = "文件全名")
  private String fullName;

  @NotNull
  @NotBlank
  @Schema(title = "文件 MIME")
  private String mimeType = MediaTypes.BINARY.media();

  @NotBlank
  @Schema(title = "保存文件名称")
  private String saveName = fullName;

  @Schema(title = "文件描述")
  private String doc;

  @NotBlank
  @Schema(title = "url")
  private String url;


  @NotBlank
  @Schema(title = "文件夹", defaultValue = "/")
  private String dir = "/";

  @Schema(title = "是否为远程存储")
  private Boolean rnType = true;

  @Min(1)
  @Schema(title = "文件大小", defaultValue = "0")
  private Long size = 0L;
}
