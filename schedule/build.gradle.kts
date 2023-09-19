import net.yan100.compose.plugin.V

project.version = libs.versions.compose.schedule.get()

dependencies {
  //api("com.xuxueli:xxl-job-core:${V.Schedule.xxlJob}")
  implementation(project(":core"))
}
