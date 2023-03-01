dependencies {
  api(V.Component.pkgV("core"))
  api("org.freemarker:freemarker:${V.Template.freemarker}")
  api("cn.hutool:hutool-db:${V.Util.huTool}")
  runtimeOnly("com.mysql:mysql-connector-j:${V.Driver.mysql}")
}
