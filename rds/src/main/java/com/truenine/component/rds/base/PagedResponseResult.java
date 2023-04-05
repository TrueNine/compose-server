package com.truenine.component.rds.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页数据包装
 *
 * @param <T> 分页参数类型
 * @author TrueNine
 * @since 2022-12-31
 */
@Data
@Schema(title = "分页列表信息")
public class PagedResponseResult<T> {
  @Schema(title = "数据列表")
  private List<T> dataList;

  @Schema(title = "结果总数")
  private Long total = 0L;

  @Schema(title = "当前页面大小")
  private Integer size = 0;

  @Schema(title = "总页数")
  private Integer pageSize = 0;

  @Schema(title = "当前页码")
  private Long offset = 0L;
}
