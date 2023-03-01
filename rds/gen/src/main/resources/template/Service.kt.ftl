<#assign ctx = ctx />
<#assign tab = tab />
package ${ctx.getServicePkg()}

/**
* ${tab.getComment()!tab.getClassName()} 服务接口
*
* @author ${ctx.getAuthor()}
* @since ${ctx.nowDay()}
*/
interface ${tab.getClassName()}${ctx.getServiceSuffix()!""} {

}
