package net.yan100.compose.rds.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.yan100.compose.rds.base.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;

import java.io.Serial;
import java.io.Serializable;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static org.hibernate.annotations.NotFoundAction.IGNORE;

/**
 * api
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "api")
@Table(name = Api.TABLE_NAME)
public class Api extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "api";
  public static final String NAME = "name";
  public static final String DOC = "doc";
  public static final String PERMISSIONS_ID = "permissions_id";
  public static final String API_PATH = "api_path";
  public static final String API_METHOD = "api_method";
  public static final String API_PROTOCOL = "api_protocol";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 名称
   */
  @Schema(title = "名称")
  @Column(name = NAME)
  @Nullable
  private String name;

  /**
   * 描述
   */
  @Schema(title = "描述")
  @Column(name = DOC)
  @Nullable
  private String doc;

  /**
   * 权限
   */
  @Nullable
  @Schema(title = "权限", requiredMode = NOT_REQUIRED)
  @ManyToOne
  @JoinColumn(name = PERMISSIONS_ID, referencedColumnName = ID, foreignKey = @ForeignKey(NO_CONSTRAINT))
  @NotFound(action = IGNORE)
  private Permissions permissions;


  /**
   * 路径
   */
  @Schema(title = "路径")
  @Column(name = API_PATH)
  @Nullable
  private String apiPath;

  /**
   * 请求方式
   */
  @Schema(title = "请求方式")
  @Column(name = API_METHOD)
  @Nullable
  private String apiMethod;

  /**
   * 请求协议
   */
  @Schema(title = "请求协议")
  @Column(name = API_PROTOCOL)
  @Nullable
  private String apiProtocol;
}