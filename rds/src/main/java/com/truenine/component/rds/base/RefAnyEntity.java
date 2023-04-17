package com.truenine.component.rds.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.truenine.component.core.consts.DataBaseBasicFieldNames;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Index;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * 任意外键dao
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
@Table(indexes = {
  @Index(name = RefAnyEntity.ARI, columnList = RefAnyEntity.ARI)
})
@RequiredArgsConstructor
@Schema(title = "任意外键类型，通常与策略模式一起使用")
public class RefAnyEntity extends BaseEntity implements Serializable {
  /**
   * 任意外键
   */
  public static final String ARI = DataBaseBasicFieldNames.ANY_REFERENCE_ID;
  /**
   * 任意类型
   */
  public static final String TYP = DataBaseBasicFieldNames.ANY_REFERENCE_TYPE;
  @Serial
  private static final long serialVersionUID = 1L;
  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = DataBaseBasicFieldNames.ANY_REFERENCE_ID)
  @Schema(title = "任意外键id")
  protected Long ari;

  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = DataBaseBasicFieldNames.ANY_REFERENCE_TYPE)
  @Schema(title = "外键类别")
  protected String typ;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    RefAnyEntity anyRefDao = (RefAnyEntity) o;
    return id != null && Objects.equals(id, anyRefDao.id);
  }
}
