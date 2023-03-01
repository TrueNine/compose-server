package com.truenine.component.rds.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.truenine.component.core.db.Bf;
import com.truenine.component.rds.listener.DeleteBackupListener;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.TenantId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * jpa顶级抽象类
 * <p>
 * \@CreateBy
 * \@LastModifyBy
 *
 * @author TrueNine
 * @since 2022-12-12
 */
@Setter
@Getter
@ToString
@DynamicInsert
@DynamicUpdate
@MappedSuperclass
@RequiredArgsConstructor
@Schema(title = "顶级抽象类")
@EntityListeners(DeleteBackupListener.class)
public class BaseDao {

  /**
   * 主键
   */
  public static final String ID = Bf.ID;

  /**
   * 创建时间
   */
  public static final String CCT = Bf.CREATE_TIME;

  /**
   * 修改时间
   */
  public static final String CMT = Bf.MODIFY_TIME;

  /**
   * 修改人
   */
  public static final String CMB = Bf.MODIFY_BY;

  /**
   * 创建人
   */
  public static final String CCB = Bf.CREATE_BY;

  /**
   * 乐观锁版本
   */
  public static final String CLV = Bf.LOCK_VERSION;

  /**
   * 逻辑删除标志
   */
  public static final String LDF = Bf.LOGIC_DELETE_FLAG;

  /**
   * 租户 id
   */
  public static final String CTI = Bf.TENANT_ID;

  @Id
  @JsonIgnore
  @Column(name = Bf.ID, columnDefinition = "BIGINT UNSIGNED")
  @Expose(deserialize = false)
  @GenericGenerator(
    name = "snowflakeId",
    strategy = "com.truenine.component.rds.autoconfig.SnowflakeIdGenerator")
  @GeneratedValue(generator = "snowflakeId")
  @Schema(name = Bf.ID,
    description = "主键id",
    defaultValue = "主键自动生成",
    example = "7001234523405")
  protected String id;

  @JsonIgnore
  @CreatedDate
  @Column(name = Bf.CREATE_TIME,
    insertable = false,
    updatable = false)
  @Expose(deserialize = false)
  @Schema(title = "创建时间")
  protected LocalDateTime cct;

  @JsonIgnore
  @LastModifiedDate
  @Column(name = Bf.MODIFY_TIME)
  @Expose(deserialize = false)
  @Schema(title = "修改时间")
  protected LocalDateTime cmt;

  @JsonIgnore
  @Column(name = Bf.CREATE_BY,
    nullable = false,
    updatable = false)
  @Expose(deserialize = false)
  @Schema(title = "创建人id")
  protected String ccb = Bf.Rbac.ROOT_ID;

  @JsonIgnore
  @Column(name = Bf.MODIFY_BY,
    insertable = false)
  @Expose(deserialize = false)
  @Schema(title = "修改人id")
  protected String cmb = Bf.Rbac.ROOT_ID;

  @Version
  @JsonIgnore
  @Column(name = Bf.LOCK_VERSION,
    nullable = false)
  @Expose(deserialize = false)
  @Schema(title = "乐观锁版本")
  protected Long clv;

  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = Bf.LOGIC_DELETE_FLAG,
    nullable = false
  )
  @Schema(title = "逻辑删除标志")
  protected Boolean ldf;

  @JsonIgnore
  @TenantId
  @Column(name = Bf.TENANT_ID,
    nullable = false,
    updatable = false)
  @Expose(deserialize = false)
  @Schema(title = "租户id", defaultValue = "0", example = "700124255456")
  protected String cti;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (BaseDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
