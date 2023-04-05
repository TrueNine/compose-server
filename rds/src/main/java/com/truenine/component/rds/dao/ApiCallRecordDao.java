package com.truenine.component.rds.dao;

import com.truenine.component.rds.base.BaseDao;
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
 * API请求记录
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
@Schema(title = "API请求记录")
@Table(name = ApiCallRecordDao.$T_NAME, indexes = {
  @Index(name = "api_id_idx", columnList = "api_id"),
})
public class ApiCallRecordDao extends BaseDao implements Serializable {

  public static final String $T_NAME = "api_call_record";
  public static final String API_ID = "api_id";
  public static final String DEVICE_CODE = "device_code";
  public static final String REQ_IP = "req_ip";
  public static final String RESP_CODE = "resp_code";
  public static final String RESP_RESULT_ENC = "resp_result_enc";
  @Serial
  private static final long serialVersionUID = 1L;
  private static final String LOGIN_IP = "login_ip";
  /**
   * api
   */
  @Schema(
    name = API_ID,
    description = "api"
  )
  @Column(table = $T_NAME,
    name = API_ID,
    nullable = false)
  private Long apiId;

  /**
   * 设备 id, 浏览器为 agent
   */
  @Schema(
    name = DEVICE_CODE,
    description = "设备 id, 浏览器为 agent"
  )
  @Column(table = $T_NAME,
    name = DEVICE_CODE)
  @Nullable
  private String deviceCode;

  /**
   * 请求 ip
   */
  @Schema(
    name = REQ_IP,
    description = "请求 ip"
  )
  @Column(table = $T_NAME,
    name = REQ_IP)
  @Nullable
  private String reqIp;

  /**
   * 登录 ip
   */
  @Schema(
    name = LOGIN_IP,
    description = "登录 ip"
  )
  @Column(table = $T_NAME,
    name = LOGIN_IP)
  @Nullable
  private String loginIp;

  /**
   * 响应码
   */
  @Schema(
    name = RESP_CODE,
    description = "响应码"
  )
  @Column(table = $T_NAME,
    name = RESP_CODE)
  @Nullable
  private String respCode;

  /**
   * 请求结果
   */
  @Schema(
    name = RESP_RESULT_ENC,
    description = "请求结果"
  )
  @Column(table = $T_NAME,
    name = RESP_RESULT_ENC)
  @Nullable
  private String respResultEnc;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (ApiCallRecordDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
