<#-- @ftlvariable name="root" type="java.util.List<net.yan100.compose.meta.client.ClientType>" -->
<#list root as r>
  export const ${r.typeName} = {
  <#list r.enumConstants?keys as k>
    ${k}: ${r.enumConstants[k]}
  </#list>
  } as const

  export enum ${r.typeName} {
  <#list r.enumConstants?keys as k>
    ${k} = ${r.enumConstants[k]}
  </#list>
  }
</#list>
