<#assign ctx = ctx />
<#assign tab = tab />
package ${ctx.getEntityPkg()};

import ${ctx.getBaseEntityClassType()};
<#if tab.getIdx()?? && (tab.getIdx()?size>0)>
import jakarta.persistence.Index;
</#if>
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
<#list tab.getImports() as t>
import ${t};
</#list>

/**
 * ${tab.getComment()!tab.getClassName()}
 *
 * @author ${ctx.getAuthor()}
 * @since ${ctx.nowDay()}
 */
@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "${tab.getEscapeComment()!tab.getClassName()}")
@Table(name = ${tab.getClassName()}${ctx.getEntitySuffix()!""}.TABLE_NAME)
public class ${tab.getClassName()}${ctx.getEntitySuffix()!""} extends ${ctx.getBaseEntityClassName()} implements Serializable {
  public static final String TABLE_NAME = "${tab.getName()}";
<#-- 静态表字段名 -->
<#list tab.getColumns() as col>
  public static final String ${col.getUpperName()} = "${col.getColName()}";
</#list>
  @Serial
  private static final long serialVersionUID = 1L;
<#-- 表字段 -->
<#list tab.getColumns() as col>
  /**
   * ${col.getComment()!col.getFieldName()}
   */<#if col.getNullable()>
  @Nullable</#if>
  @Schema(title = "${col.getComment()!col.getFieldName()}")
  @Column(name = ${col.getUpperName()}<#if !col.getNullable()>,nullable = false</#if><#if col.getUnique()>,unique = true</#if>)
  private ${col.getJavaType()} ${col.getFieldName()};
</#list>
}
