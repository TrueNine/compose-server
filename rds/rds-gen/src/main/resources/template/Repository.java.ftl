<#assign ctx = ctx />
<#assign tab = tab />
package ${ctx.getRepositoryPkg()};

import ${ctx.getEntityPkg()}.${tab.getClassName()}${ctx.getEntitySuffix()!""};
import ${ctx.getBaseRepositoryClassType()};
import org.springframework.stereotype.Repository;

/**
 * ${tab.getComment()!tab.getClassName()} Repository 操作接口
 *
 * @author ${ctx.getAuthor()}
 * @since ${ctx.nowDay()}
 */
@Repository
public interface ${tab.getClassName()}${ctx.getRepositorySuffix()!""} extends ${ctx.getBaseRepositoryClassName()}<${tab.getClassName()}${ctx.getEntitySuffix()!""}, String> {

}