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
@Table(name = ${tab.getClassName()}${ctx.getEntitySuffix()!""}.TABLE_NAME)
data class ${tab.getClassName()}${ctx.getEntitySuffix()!""} (
<#-- 表字段 -->
<#list tab.getColumns() as col>
/**
* ${col.getComment()!col.getFieldName()}
*/
@Schema(title="${col.getEscapeComment()}")
@Column(name = ${col.getUpperName()}<#if !col.getNullable()>,nullable = false</#if><#if col.getUnique()>,unique = true</#if>)
var ${col.getFieldName()}: ${col.getJavaType()}? = null
</#list>
) : ${ctx.getBaseEntityClassName()}(), Serializable {
companion object {
/**
* serialVersionUID
*/
@Serial
const val serialVersionUID = 1L;
/**
* ${tab.getName()} 表名
*/
const val TABLE_NAME = "${tab.getName()}"
<#-- 静态表字段名 -->
<#list tab.getColumns() as col>
/**
* ${col.getComment()!col.getFieldName()} 列
*/
const val ${col.getUpperName()} = "${col.getColName()}"
</#list>
}
}
