import net.yan100.compose.plugin.V

project.version = V.Compose.SCHEDULE

dependencies {
  //api("com.xuxueli:xxl-job-core:${V.Schedule.xxlJob}")
  implementation(project(":core"))
}
