package io.tn.rds.dto;

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
public class PagedData<T> {
  @Schema(title = "分页数据列表")
  private List<T> contents;

  @Schema(title = "结果总数")
  private Long totalSize = 0L;

  @Schema(title = "当前页面大小")
  private Integer currentSize = 0;

  @Schema(title = "总页码")
  private Integer totalPage = 0;

  @Schema(title = "当前页码")
  private Long offset = 0L;
}
