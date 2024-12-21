<#-- @ftlvariable name="root" type="java.util.List<net.yan100.compose.meta.client.ClientType>" -->
<#list root as r>
  export type = ${r.typeName} = ${r.typeName}
</#list>
