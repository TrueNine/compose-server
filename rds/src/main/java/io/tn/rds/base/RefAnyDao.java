package io.tn.rds.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import io.swagger.v3.oas.annotations.media.Schema;
import io.tn.core.db.Bf;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
@RequiredArgsConstructor
@Schema(title = "任意外键类型，通常与策略模式一起使用")
public class RefAnyDao extends BaseDao {

  /**
   * 任意外键
   */
  public static final String ARI = Bf.ANY_REFERENCE_ID;

  /**
   * 任意类型
   */
  public static final String TYP = Bf.ANY_REFERENCE_TYPE;

  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = Bf.ANY_REFERENCE_ID)
  @Schema(title = "任意外键id")
  protected String ari;

  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = Bf.ANY_REFERENCE_TYPE)
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
    RefAnyDao anyRefDao = (RefAnyDao) o;
    return id != null && Objects.equals(id, anyRefDao.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
