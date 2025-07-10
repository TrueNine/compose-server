package io.github.truenine.composeserver.gradleplugin.generator

import io.github.truenine.composeserver.gradleplugin.consts.Repos

enum class MavenRepoType(
  val mavenCentralUrl: String? = null,
  val googlePluginUrl: String? = null,
  val jCenterUrl: String? = null,
  val gradlePluginUrl: String? = null,
  val gradleDistributionUrl: String? = null,
) {
  ALIYUN(mavenCentralUrl = Repos.aliCentral, gradlePluginUrl = Repos.aliGradlePlugin, googlePluginUrl = Repos.aliGoogle, jCenterUrl = Repos.aliJCenter),
  TENCENT_CLOUD(mavenCentralUrl = Repos.tencentCloudMavenPublic, googlePluginUrl = Repos.tencentCloudMavenPublic, gradlePluginUrl = Repos.aliGradlePlugin),
  HUAWEI_CLOUD(mavenCentralUrl = Repos.huaweiCloudMaven),
  DEFAULT,
}
