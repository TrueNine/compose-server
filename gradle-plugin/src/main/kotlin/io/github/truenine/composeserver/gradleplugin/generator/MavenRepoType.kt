package io.github.truenine.composeserver.gradleplugin.generator

import io.github.truenine.composeserver.gradleplugin.consts.MavenRepositoryUrls

enum class MavenRepoType(
  val mavenCentralUrl: String? = null,
  val googlePluginUrl: String? = null,
  val jCenterUrl: String? = null,
  val gradlePluginUrl: String? = null,
  val gradleDistributionUrl: String? = null,
) {
  ALIYUN(
    mavenCentralUrl = MavenRepositoryUrls.ALIYUN_CENTRAL,
    gradlePluginUrl = MavenRepositoryUrls.ALIYUN_GRADLE_PLUGIN,
    googlePluginUrl = MavenRepositoryUrls.ALIYUN_GOOGLE,
    jCenterUrl = MavenRepositoryUrls.ALIYUN_JCENTER,
  ),
  TENCENT_CLOUD(
    mavenCentralUrl = MavenRepositoryUrls.TENCENT_MAVEN_PUBLIC,
    googlePluginUrl = MavenRepositoryUrls.TENCENT_MAVEN_PUBLIC,
    gradlePluginUrl = MavenRepositoryUrls.ALIYUN_GRADLE_PLUGIN,
  ),
  HUAWEI_CLOUD(mavenCentralUrl = MavenRepositoryUrls.HUAWEI_CLOUD_MAVEN),
  DEFAULT,
}
