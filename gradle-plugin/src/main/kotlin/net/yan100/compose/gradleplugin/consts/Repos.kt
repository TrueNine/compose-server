package net.yan100.compose.gradleplugin.consts

/** maven 仓库管理 */
@Deprecated("无需使用")
object Repos {
  const val tencentCloudMavenPublic = "https://mirrors.cloud.tencent.com/repository/maven-public/"

  private const val ALI_REPO = "https://maven.aliyun.com/repository/"

  const val aliJCenter = "${ALI_REPO}jcenter"
  const val aliCentral = "${ALI_REPO}central"
  const val aliPublic = "${ALI_REPO}public"
  const val aliGoogle = "${ALI_REPO}google"
  const val aliGradlePlugin = "${ALI_REPO}gradle-plugin"
  const val aliSpring = "${ALI_REPO}spring"
  const val aliSpringPlugin = "${ALI_REPO}spring-plugin"
  const val aliGrailsCore = "${ALI_REPO}grails-core"
  const val aliApacheSnapshots = "${ALI_REPO}apache-snapshots"

  private const val HUAWEI_REPO = "https://repo.huaweicloud.com/repository/"

  const val huaweiCloudMaven = "${HUAWEI_REPO}maven"

  private const val SPRING_REPO = "https://repo.spring.io/"

  const val springMilestone = "${SPRING_REPO}milestone"
  const val springLibMilestone = "${SPRING_REPO}libs-milestone"
  const val springSnapshot = "${SPRING_REPO}snapshot"
  const val mybatisPlusSnapshot = "https://oss.sonatype.org/content/repositories/snapshots/"

  val publicRepositories =
    listOf(
      huaweiCloudMaven,
      aliPublic,
      aliCentral,
      aliJCenter,
      aliGoogle,
      aliGradlePlugin,
      aliSpring,
      aliSpringPlugin,
      aliGrailsCore,
      aliApacheSnapshots,
      tencentCloudMavenPublic,
      springMilestone,
      springLibMilestone,
      springSnapshot,
      mybatisPlusSnapshot,
    )
}
