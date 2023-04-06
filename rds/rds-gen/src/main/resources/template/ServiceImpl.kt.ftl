<#assign ctx = ctx />
<#assign tab = tab />
package ${ctx.getServiceImplPkg()}

import io.tn.core.lang.LogKt
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
@Transactional(rollbackFor = [Exception::class])
open class ${tab.getClassName()}${ctx.getServiceImplSuffix()!""} (
private val repo: ${tab.getClassName()}${ctx.getRepositorySuffix()!""}
) : ${tab.getClassName()}${ctx.getServiceSuffix()!""} {

companion object {
@JvmStatic
private val log = LogKt.getLog(${tab.getClassName()}${ctx.getServiceImplSuffix()!""}::class)
}
}
