package net.yan100.compose.rds.core.models

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serial
import java.io.Serializable

/**
 * 分页数据包装
 *
 * @param <T> 分页参数类型
 * @author TrueNine
 * @since 2022-12-31
 */
@Schema(title = "分页列表信息")
data class PagedResponseResult<T>(
    @Schema(title = "数据列表")
    var dataList: List<T> = mutableListOf(),

    @get:Schema(title = "结果总数")
    var total: Long = 0L,

    @Schema(title = "当前页面大小")
    var size: Int = 0,

    @Schema(title = "总页数")
    var pageSize: Int = 0,

    @Schema(title = "当前页码")
    var offset: Long = 0L,
) : Serializable {

    companion object {
        @Serial
        private val serialVersionUID = 1L

        fun <T> empty(): PagedResponseResult<T> {
            val r = PagedResponseResult<T>()
            r.dataList = mutableListOf()
            r.offset = 0L
            r.pageSize = 0
            r.size = 0
            r.total = 0
            return r
        }
    }
}
