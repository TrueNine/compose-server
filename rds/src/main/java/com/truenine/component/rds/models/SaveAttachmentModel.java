package com.truenine.component.rds.models;

import com.truenine.component.rds.typing.AttachmentStorageTyping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "记录文件")
public class SaveAttachmentModel {

  @Schema(title = "存储的url")
  private String baseUrl;

  @Schema(title = "保存后的名称")
  private String saveName;

  @NotNull(message = "请设置存储级别")
  @Schema(title = "存储类别")
  private AttachmentStorageTyping storageType = AttachmentStorageTyping.NATIVE;
}
