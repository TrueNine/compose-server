/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.plugin.generator

import net.yan100.compose.plugin.consts.Repos

enum class MavenRepoType(
  val mavenCentralUrl: String? = null,
  val googlePluginUrl: String? = null,
  val jCenterUrl: String? = null,
  val gradlePluginUrl: String? = null,
  val gradleDistributionUrl: String? = null,
) {
  ALIYUN(mavenCentralUrl = Repos.aliCentral, gradlePluginUrl = Repos.aliGradlePlugin, googlePluginUrl = Repos.aliGoogle, jCenterUrl = Repos.aliJCenter),
  TENCENT_CLOUD(
    mavenCentralUrl = Repos.tencentCloudMavenPublic,
    googlePluginUrl = Repos.tencentCloudMavenPublic,
    gradlePluginUrl = Repos.tencentCloudGradlePlugin,
  ),
  HUAWEI_CLOUD(mavenCentralUrl = Repos.huaweiCloudMaven),
  DEFAULT,
}
