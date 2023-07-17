package net.yan100.compose.rds.base;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.yan100.compose.core.consts.DataBaseBasicFieldNames;
import net.yan100.compose.core.lang.Str;
import net.yan100.compose.rds.listener.BizCodeInsertListener;
import net.yan100.compose.rds.listener.PreSaveDeleteReferenceListener;
import net.yan100.compose.rds.listener.SnowflakeIdInsertListener;
import net.yan100.compose.rds.listener.TableRowDeletePersistenceListener;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Persistable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * JPA的最基础基类，包括一个 id
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@Setter
@Getter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "顶级任意抽象类")
@EntityListeners({
  TableRowDeletePersistenceListener.class,
  BizCodeInsertListener.class,
  SnowflakeIdInsertListener.class,
  PreSaveDeleteReferenceListener.class
})
public class AnyEntity implements Serializable, Persistable<String> {
  /**
   * 主键
   */
  public static final String ID = DataBaseBasicFieldNames.ID;

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * id
   */
  @Id
  @Column(name = DataBaseBasicFieldNames.ID)
  @Schema(title = ID, example = "7001234523405")
  protected String id;

  @org.jetbrains.annotations.NotNull
  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    AnyEntity that = (AnyEntity) o;
    return id != null && (!"".equals(id)) && (!"null".equals(id)) && Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  public void asNew() {
    this.id = "";
  }

  @Override
  public String toString() {
    return id == null ? "null" : id;
  }

  @Override
  @Transient
  @JsonIgnore
  public boolean isNew() {
    return Str.nonText(id) || "null".equals(id);
  }
}
