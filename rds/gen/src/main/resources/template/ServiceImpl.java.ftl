<#assign ctx = ctx />
<#assign tab = tab />
package ${ctx.getServiceImplPkg()};

import lombok.extern.slf4j.Slf4j;
import ${ctx.getServicePkg()}.${tab.getClassName()}${ctx.getServiceSuffix()!""};
import ${ctx.getRepositoryPkg()}.${tab.getClassName()}${ctx.getRepositorySuffix()!""};
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* ${tab.getComment()!tab.getClassName()} 服务实现
*
* @author ${ctx.getAuthor()}
* @since ${ctx.nowDay()}
*/
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ${tab.getClassName()}${ctx.getServiceImplSuffix()!""}
implements ${tab.getClassName()}${ctx.getServiceSuffix()!""} {

private final ${tab.getClassName()}${ctx.getRepositorySuffix()!""} repo;

public ${tab.getClassName()}${ctx.getServiceImplSuffix()!""}(
${tab.getClassName()}${ctx.getRepositorySuffix()!""} repo
) {
this.repo = repo;
}

}
