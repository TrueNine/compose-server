package io.tn.rds.dao;

import io.tn.rds.base.BaseDao;
import org.hibernate.Hibernate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 消息 文件
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
@Schema(title = "消息 文件")
@Table(name = MessageFileDao.$T_NAME)
public class MessageFileDao extends BaseDao implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  public static final String $T_NAME = "message_file";

  public static final String MESSAGE_ID = "message_id";

  public static final String FILE_ID = "file_id";

  /**
   * message
   */
  @Schema(
    name = MESSAGE_ID,
    description = "message"
  )
  @Column(table = $T_NAME,
    name = MESSAGE_ID,
    nullable = false)
  private Long messageId;

  /**
   * 文件
   */
  @Schema(
    name = FILE_ID,
    description = "文件"
  )
  @Column(table = $T_NAME,
    name = FILE_ID,
    nullable = false)
  private Long fileId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (MessageFileDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
