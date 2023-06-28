package net.yan100.compose.rds.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.yan100.compose.core.consts.DataBaseBasicFieldNames;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;

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
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@Schema(title = "顶级抽象类")
public class BaseEntity extends AnyEntity {
  /**
   * 乐观锁版本
   */
  public static final String RLV = DataBaseBasicFieldNames.LOCK_VERSION;
  /**
   * 逻辑删除标志
   */
  public static final String LDF = DataBaseBasicFieldNames.LOGIC_DELETE_FLAG;


  /**
   * 乐观锁版本
   */
  @Version
  @JsonIgnore
  @Column(name = RLV, nullable = false, columnDefinition = "INT DEFAULT 0")
  @Schema(hidden = true, title = "乐观锁版本", requiredMode = NOT_REQUIRED)
  protected Long rlv;

  /**
   * 逻辑删除标志
   */
  @JsonIgnore
  @Column(name = LDF, columnDefinition = "INT DEFAULT 0")
  @Schema(hidden = true, title = "逻辑删除标志", requiredMode = NOT_REQUIRED, accessMode = READ_ONLY)
  protected Boolean ldf;

  @Override
  public void asNew() {
    super.asNew();
    ldf = false;
    rlv = null;
  }
}
