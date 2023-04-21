<#assign ctx = ctx />
<#assign tab = tab />
package ${ctx.getServiceImplPkg()}

import com.truenine.component.core.lang.LogKt
import ${ctx.getServicePkg()}.${tab.getClassName()}${ctx.getServiceSuffix()!""}
import ${ctx.getRepositoryPkg()}.${tab.getClassName()}${ctx.getRepositorySuffix()!""}
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * ${tab.getComment()!tab.getClassName()} 服务实现
 *
 * @author ${ctx.getAuthor()}
 * @since ${ctx.nowDay()}
 */
@Service
class ${tab.getClassName()}${ctx.getServiceImplSuffix()!""} (
  private val repo: ${tab.getClassName()}${ctx.getRepositorySuffix()!""}
) : ${tab.getClassName()}${ctx.getServiceSuffix()!""} {
  private val log = LogKt.getLog(this::class)

}
