package io.tn.rds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class PageParam {

  @Min(0)
  @Schema(title = "页码 最小为 0", defaultValue = "0")
  private Integer offset = 0;

  @Min(1)
  @Max(42)
  @Schema(title = "页面大小，最大 42，最小 1", defaultValue = "42")
  private Integer pageSize = 42;
}
