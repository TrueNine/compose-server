package io.github.truenine.composeserver.gradleplugin.consts

/**
 * maven repository management
 *
 * @author TrueNine
 * @since 2023-08-06
 */
object MavenRepositoryUrls {
  const val TENCENT_MAVEN_PUBLIC = "https://mirrors.cloud.tencent.com/repository/maven-public/"

  private const val ALI_REPO = "https://maven.aliyun.com/repository/"

  const val ALIYUN_JCENTER = "${ALI_REPO}jcenter"
  const val ALIYUN_CENTRAL = "${ALI_REPO}central"
  const val ALIYUN_PUBLIC = "${ALI_REPO}public"
  const val ALIYUN_GOOGLE = "${ALI_REPO}google"
  const val ALIYUN_GRADLE_PLUGIN = "${ALI_REPO}gradle-plugin"
  const val ALIYUN_SPRING = "${ALI_REPO}spring"
  const val ALIYUN_SPRING_PLUGIN = "${ALI_REPO}spring-plugin"
  const val ALIYUN_GRADLE_CORE = "${ALI_REPO}grails-core"
  const val ALIYUN_APACHE_SNAPSHOTS = "${ALI_REPO}apache-snapshots"

  private const val HUAWEI_REPO = "https://repo.huaweicloud.com/repository/"

  const val HUAWEI_CLOUD_MAVEN = "${HUAWEI_REPO}maven"

  private const val SPRING_REPO = "https://repo.spring.io/"

  const val SPRING_MILESTONE = "${SPRING_REPO}milestone"
  const val SPRING_LIB_MILESTONE = "${SPRING_REPO}libs-milestone"
  const val SPRING_SNAPSHOT = "${SPRING_REPO}snapshot"
  const val MYBATIS_PLUGS_SNAPSHOT = "https://oss.sonatype.org/content/repositories/snapshots/"

  val publicRepositories =
    listOf(
      HUAWEI_CLOUD_MAVEN,
      ALIYUN_PUBLIC,
      ALIYUN_CENTRAL,
      ALIYUN_JCENTER,
      ALIYUN_GOOGLE,
      ALIYUN_GRADLE_PLUGIN,
      ALIYUN_SPRING,
      ALIYUN_SPRING_PLUGIN,
      ALIYUN_GRADLE_CORE,
      ALIYUN_APACHE_SNAPSHOTS,
      TENCENT_MAVEN_PUBLIC,
      SPRING_MILESTONE,
      SPRING_LIB_MILESTONE,
      SPRING_SNAPSHOT,
      MYBATIS_PLUGS_SNAPSHOT,
    )
}
