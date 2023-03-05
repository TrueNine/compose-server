package com.truenine.component.rds.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.truenine.component.rds.converters.AesEncryptConverter;
import com.truenine.component.rds.dao.FileDao;
import com.truenine.component.rds.dao.FileLocationDao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * 文件出参
 *
 * @author TrueNine
 * @since 2023-01-01
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = FileDao.$T_NAME)
@SecondaryTable(
  name = FileLocationDao.$T_NAME,
  foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
)
@Schema(title = "文件")
public class FileVo {

  @Id
  @Schema(hidden = true)
  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = FileDao.ID)
  private String id;

  @Schema(title = "文件名")
  @Column(name = FileDao.META_NAME)
  @Convert(converter = AesEncryptConverter.class)
  private String name;

  @Schema(
    title = "文件大小 byte",
    name = "byteSize",
    defaultValue = "0",
    description = "文件大小 byte"
  )
  @Column(name = FileDao.BYTE_SIZE)
  private Long byteSize;

  @Schema(
    title = "mime-type 类型",
    name = "mimeType",
    nullable = true,
    example = "text/html",
    defaultValue = "application/octet-stream",
    description = "mime-type 类型"
  )
  @Column(name = FileDao.MIME_TYPE)
  private String mimeType;

  @Column(
    table = FileLocationDao.$T_NAME,
    name = FileLocationDao.URL
  )
  @JoinTable(
    name = FileLocationDao.$T_NAME,
    foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
    joinColumns = {@JoinColumn(
      name = FileLocationDao.ID,
      referencedColumnName = FileDao.FILE_LOCATION_ID
    )}
  )
  @Schema(hidden = true)
  @JsonIgnore
  @Expose(deserialize = false)
  @NotFound(action = NotFoundAction.IGNORE)
  private String url;

  @Column(name = FileDao.SAVE_NAME)
  @NotFound(action = NotFoundAction.IGNORE)
  @JsonIgnore
  @Expose(deserialize = false)
  private String saveName;


  @Transient
  @Schema(
    name = "fullUrl",
    description = "访问的全路径")
  public String getFullUrl() {
    return this.url + "/" + this.saveName;
  }
}
