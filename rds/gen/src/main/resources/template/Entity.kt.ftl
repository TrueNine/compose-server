<#assign ctx = ctx />
<#assign tab = tab />
package ${ctx.getEntityPkg()}

import ${ctx.getBaseEntityClassType()}
<#if tab.getIdx()?? && (tab.getIdx()?size>0)>
  import jakarta.persistence.Index
</#if>
import org.hibernate.Hibernate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
<#list tab.getImports() as t>
  import ${t}
</#list>

/**
* ${tab.getComment()!tab.getClassName()}
*
* @author ${ctx.getAuthor()}
* @since ${ctx.nowDay()}
*/
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "${tab.getEscapeComment()!tab.getClassName()}")
@Table(name = ${tab.getClassName()}${ctx.getEntitySuffix()!""}.THIS_TABLE_NAME<#if tab.getIdx()?? && (tab.getIdx()?size > 0)>, indexes = {
    <#list tab.getIdx() as idx>
      @Index(name = "${idx.getKeyName()!idx.getColumnName()}", columnList = "${idx.getColumnName()}"),
    </#list>
  }</#if>)
data class ${tab.getClassName()}${ctx.getEntitySuffix()!""} : ${ctx.getBaseEntityClassName()}(), Serializable {

<#-- 静态表字段名 -->
<#list tab.getColumns() as col>
  /**
  * ${col.getComment()!col.getFieldName()} 列
  */
  public static final String ${col.getUpperName()} = "${col.getColName()}";

</#list>
/**
* serialVersionUID
*/
@Serial
private static final long serialVersionUID = 1L;

/**
* ${tab.getName()} 表名
*/
public static final String THIS_TABLE_NAME = "${tab.getName()}";

<#-- 表字段 -->
<#list tab.getColumns() as col>
  /**
  * ${col.getComment()!col.getFieldName()}
  */
  @Schema(
  name = "${col.getFieldName()}",
  title="${col.getEscapeComment()}",
  description="${col.getEscapeComment()}"
  )
  @Column(table = THIS_TABLE_NAME,
  name = ${col.getUpperName()}<#if !col.getNullable()>,
  nullable = false</#if><#if col.getUnique()>,
  unique = true</#if>)
  private ${col.getJavaType()} ${col.getFieldName()};

</#list>
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    val that = (${tab.getClassName()}${ctx.getEntitySuffix()}) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  open fun hashCode() : Int =this::class.hashCode;

}
