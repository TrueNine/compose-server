package com.truenine.component.rds.dao;

import com.truenine.component.rds.base.RefAnyDao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
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
 * 消息
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
@Schema(title = "消息")
@Table(name = MessageDao.$T_NAME, indexes = {
  @Index(name = "send_user_id_idx", columnList = "send_user_id"),
  @Index(name = "typ_idx", columnList = "typ"),
  @Index(name = "ari_idx", columnList = "ari"),
})
public class MessageDao extends RefAnyDao implements Serializable {

  public static final String $T_NAME = "message";
  public static final String MSG = "msg";
  public static final String SEND_USER_ID = "send_user_id";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 消息
   */
  @Schema(
    name = MSG,
    description = "消息"
  )
  @Column(table = $T_NAME,
    name = MSG)
  @Nullable
  private String msg;

  /**
   * 发送方
   */
  @Schema(
    name = SEND_USER_ID,
    description = "发送方"
  )
  @Column(table = $T_NAME,
    name = SEND_USER_ID,
    nullable = false)
  private Long sendUserId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (MessageDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
