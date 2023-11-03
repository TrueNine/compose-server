package net.yan100.compose.rds.core.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页入参
 *
 * @author TrueNine
 * @since 2022-12-31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "分页请求入参")
public class PagedRequestParam implements Serializable {
  public static final int MIN_OFFSET = 0;
  public static final int MAX_PAGE_SIZE = 42;

  @Serial
  private static final long serialVersionUID = 1L;

  @Nullable
  @Min(value = MIN_OFFSET, message = "分页页码最小为0")
  @Schema(title = "页码 最小为 0", defaultValue = "0")
  private Integer offset = MIN_OFFSET;


  @Nullable
  @Min(value = 1, message = "页面大小最小为1")
  @Max(value = MAX_PAGE_SIZE, message = "分页最大参数为" + MAX_PAGE_SIZE)
  @Schema(title = "页面大小，最大 " + MAX_PAGE_SIZE + "，最小 1", defaultValue = MAX_PAGE_SIZE + "")
  private Integer pageSize = MAX_PAGE_SIZE;


  @Schema(title = "取消分页请求", defaultValue = "false")
  private Boolean unPage = false;
}
