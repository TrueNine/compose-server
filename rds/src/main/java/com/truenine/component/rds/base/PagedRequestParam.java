package com.truenine.component.rds.base;

import com.truenine.component.rds.util.PagedWrapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class PagedRequestParam {
  @NotNull
  @Min(0)
  @Schema(title = "页码 最小为 0", defaultValue = "0")
  private Integer offset;

  @NotNull
  @Min(1)
  @Max(PagedWrapper.MAX_PAGE_SIZE)
  @Schema(title = "页面大小，最大 42，最小 1", defaultValue = "42")
  private Integer pageSize;
}