package net.yan100.compose.rds.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.yan100.compose.rds.base.BaseEntity;
import net.yan100.compose.rds.base.RecordModel;
import net.yan100.compose.rds.converters.RecordModelConverter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.time.LocalDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;

/**
 * 数据删除备份表
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "数据删除记录")
@Table(name = TableRowDeleteRecord.TABLE_NAME)
public class TableRowDeleteRecord extends BaseEntity {

  public static final String TABLE_NAME = "table_row_delete_record";
  public static final String TABLE_NAMES = "table_names";
  public static final String USER_ID = "user_id";
  public static final String USER_ACCOUNT = "user_account";
  public static final String DELETE_DATETIME = "delete_datetime";
  public static final String ENTITY = "entity";

  @Serial
  private static final long serialVersionUID = 1L;

  @Schema(title = "表名", requiredMode = NOT_REQUIRED)
  @Column(name = TABLE_NAMES, nullable = false)
  private String tableNames;

  @Nullable
  @Schema(title = "删除用户id")
  @Column(name = USER_ID)
  private String userId;

  @Nullable
  @Schema(title = "删除用户账户")
  @Column(name = USER_ACCOUNT)
  private String userAccount;

  @Schema(title = "删除时间")
  @Column(name = DELETE_DATETIME, nullable = false)
  private LocalDateTime deleteDatetime;

  @Nullable
  @Convert(converter = RecordModelConverter.class)
  @Schema(title = "删除实体")
  @Column(name = ENTITY, columnDefinition = "VARCHAR(10240)")
  private RecordModel entity;
}
