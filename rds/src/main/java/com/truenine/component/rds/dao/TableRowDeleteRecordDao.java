package com.truenine.component.rds.dao;

import com.truenine.component.rds.base.BaseDao;
import com.truenine.component.rds.converters.TableRowChangeRecordConverter;
import com.truenine.component.rds.models.TableRowChangeSerializableObjectModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 数据删除备份表
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@ToString
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "数据删除记录")
@Table(name = TableRowDeleteRecordDao.$T_NAME, indexes = {
  @Index(
    name = TableRowDeleteRecordDao.TABLE_NAME,
    columnList = TableRowDeleteRecordDao.TABLE_NAME
  ),
  @Index(
    name = TableRowDeleteRecordDao.USER_ID,
    columnList = TableRowDeleteRecordDao.USER_ID
  ),
  @Index(
    name = TableRowDeleteRecordDao.USER_ACCOUNT,
    columnList = TableRowDeleteRecordDao.USER_ACCOUNT
  )
})
public class TableRowDeleteRecordDao extends BaseDao implements Serializable {

  public static final String $T_NAME = "table_row_delete_record";
  public static final String TABLE_NAME = "table_name";
  public static final String USER_ID = "user_id";
  public static final String USER_ACCOUNT = "user_account";
  public static final String DELETE_DATETIME = "delete_datetime";
  public static final String ENTITY = "entity";
  @Serial
  private static final long serialVersionUID = 1L;

  @Schema(title = TABLE_NAME, description = "表名")
  @Column(table = $T_NAME, name = TABLE_NAME, nullable = false)
  private String tableName;

  @Schema(title = USER_ID, description = "删除用户id")
  @Column(table = $T_NAME, name = USER_ID, nullable = false)
  private String userId;

  @Schema(title = USER_ACCOUNT, description = "删除用户账户")
  @Column(table = $T_NAME, name = USER_ACCOUNT, nullable = false)
  private String userAccount;

  @Schema(title = DELETE_DATETIME, description = "删除时间")
  @Column(table = $T_NAME, name = DELETE_DATETIME, nullable = false)
  private LocalDateTime deleteDatetime;

  @Nullable
  @Convert(converter = TableRowChangeRecordConverter.class)
  @Schema(title = ENTITY, description = "删除实体")
  @Column(table = $T_NAME, name = ENTITY)
  private TableRowChangeSerializableObjectModel entity;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (TableRowDeleteRecordDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
