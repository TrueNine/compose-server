dependencies {
  implementation(project(":core"))
  implementation(project(":pay"))
  implementation(project(":depend:depend-web-servlet"))
  implementation(project(":depend:depend-web-client"))

  implementation(("cn.hutool:hutool-core:${V.Util.huTool}"))
  implementation(("cn.hutool:hutool-crypto:${V.Util.huTool}"))
}