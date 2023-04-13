package com.truenine.component.rds.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.truenine.component.core.consts.DataBaseBasicFieldNames;
import com.truenine.component.rds.autoconfig.SnowflakeIdGeneratorBean;
import com.truenine.component.rds.listener.TableRowDeletePersistenceListener;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@ToString
@DynamicInsert
@DynamicUpdate
@MappedSuperclass
@RequiredArgsConstructor
@Schema(title = "顶级任意抽象类")
@EntityListeners(TableRowDeletePersistenceListener.class)
public class DbAnyModel implements Serializable {
  /**
   * 主键
   */
  public static final String ID = DataBaseBasicFieldNames.ID;
  @Serial
  private static final long serialVersionUID = 1L;
  @Id
  @JsonIgnore
  @Column(name = DataBaseBasicFieldNames.ID, columnDefinition = "BIGINT UNSIGNED")
  @Expose(deserialize = false)
  @GenericGenerator(
    name = SnowflakeIdGeneratorBean.NAME,
    strategy = SnowflakeIdGeneratorBean.CLASS_NAME
  )
  @Basic
  @GeneratedValue(generator = SnowflakeIdGeneratorBean.NAME)
  @Schema(name = ID,
    description = "主键id",
    defaultValue = "主键自动生成",
    example = "7001234523405")
  protected Long id;
}
