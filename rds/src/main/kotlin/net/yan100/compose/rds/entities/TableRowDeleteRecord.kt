package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.annotation.Nullable
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.datetime
import net.yan100.compose.core.alias.string
import net.yan100.compose.rds.converters.RecordModelConverter
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.models.DataRecord
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

/**
 * 数据删除备份表
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "数据删除记录")
@Table(name = TableRowDeleteRecord.TABLE_NAME)
class TableRowDeleteRecord : IEntity() {
    companion object {
        const val TABLE_NAME = "table_row_delete_record"

        const val TABLE_NAMES = "table_names"
        const val USER_ID = "user_id"
        const val USER_ACCOUNT = "user_account"
        const val DELETE_DATETIME = "delete_datetime"
        const val ENTITY = "entity"
    }

    @Schema(title = "表名", requiredMode = RequiredMode.NOT_REQUIRED)
    @Column(name = TABLE_NAMES, nullable = false)
    lateinit var tableNames: String

    @Nullable
    @Schema(title = "删除用户id")
    @Column(name = USER_ID)
    var userId: RefId? = null

    @Nullable
    @Schema(title = "删除用户账户")
    @Column(name = USER_ACCOUNT)
    var userAccount: string? = null

    @Schema(title = "删除时间")
    @Column(name = DELETE_DATETIME, nullable = false)
    lateinit var deleteDatetime: datetime

    @Nullable
    @Convert(converter = RecordModelConverter::class)
    @Schema(title = "删除实体")
    @Column(name = ENTITY, columnDefinition = "VARCHAR(10240)")
    var entity: DataRecord? = null
}
