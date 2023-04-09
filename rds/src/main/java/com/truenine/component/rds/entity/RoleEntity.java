package com.truenine.component.rds.entity;

import com.truenine.component.rds.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * 角色
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
@Schema(title = "角色")
@Table(name = RoleEntity.TABLE_NAME)
public class RoleEntity extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "role";
  public static final String NAME = "name";
  public static final String DOC = "doc";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 角色名称
   */
  @Nullable
  @Schema(name = NAME, description = "角色名称")
  @Column(table = TABLE_NAME, name = NAME)
  private String name;

  /**
   * 角色描述
   */
  @Nullable
  @Column(table = TABLE_NAME, name = DOC)
  @Schema(name = DOC, description = "角色描述")
  private String doc;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
      return false;
    RoleEntity roleDao = (RoleEntity) o;
    return getId() != null && Objects.equals(getId(), roleDao.getId());
  }
}
