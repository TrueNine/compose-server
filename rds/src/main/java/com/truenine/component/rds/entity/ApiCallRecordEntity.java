package com.truenine.component.rds.entity;

import com.truenine.component.rds.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;

import java.io.Serial;
import java.io.Serializable;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static org.hibernate.annotations.NotFoundAction.IGNORE;

/**
 * API请求记录
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "API请求记录")
@Table(name = ApiCallRecordEntity.TABLE_NAME)
public class ApiCallRecordEntity extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "api_call_record";
  public static final String API_ID = "api_id";
  public static final String DEVICE_CODE = "device_code";
  public static final String REQ_IP = "req_ip";
  public static final String RESP_CODE = "resp_code";
  public static final String RESP_RESULT_ENC = "resp_result_enc";
  @Serial
  private static final long serialVersionUID = 1L;
  private static final String LOGIN_IP = "login_ip";

  /**
   * 从属 API
   */
  @Schema(title = "API", requiredMode = NOT_REQUIRED)
  @ManyToOne
  @JoinColumn(name = API_ID, referencedColumnName = ID, foreignKey = @ForeignKey(NO_CONSTRAINT))
  @NotFound(action = IGNORE)
  private ApiEntity api;

  /**
   * 设备 id, 浏览器为 agent
   */
  @Nullable
  @Schema(title = "设备 id, 浏览器为 agent")
  @Column(name = DEVICE_CODE)
  private String deviceCode;

  /**
   * 请求 ip
   */
  @Nullable
  @Schema(title = "请求 ip")
  @Column(name = REQ_IP)
  private String reqIp;

  /**
   * 登录 ip
   */
  @Nullable
  @Schema(title = "登录 ip")
  @Column(name = LOGIN_IP)
  private String loginIp;

  /**
   * 响应码
   */
  @Nullable
  @Schema(title = "响应码")
  @Column(name = RESP_CODE)
  private Integer respCode;

  /**
   * 请求结果
   */
  @Nullable
  @Schema(title = "请求结果")
  @Column(name = RESP_RESULT_ENC)
  private String respResultEnc;
}
