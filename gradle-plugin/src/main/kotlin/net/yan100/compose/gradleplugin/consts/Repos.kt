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
package net.yan100.compose.gradleplugin.consts

/** maven 仓库管理 */
object Repos {
  object Credentials {
    val yunXiaoUsername: String = System.getenv("YUNXIAO_USER")
    val yunXiaoPassword: String = System.getenv("YUNXIAO_PWD")
  }

  const val tencentCloudMavenPublic = "https://mirrors.cloud.tencent.com/nexus/repository/maven-public/"
  const val tencentCloudGradlePlugin = "https://mirrors.cloud.tencent.com/nexus/repository/gradle-plugin/"

  const val yunXiaoRelese = "https://packages.aliyun.com/maven/repository/2336368-release-CiFRF5/"
  const val yunXiaoSnapshot = "https://packages.aliyun.com/maven/repository/2336368-snapshot-7SUFMh/"

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
      tencentCloudGradlePlugin,
      springMilestone,
      springLibMilestone,
      springSnapshot,
      mybatisPlusSnapshot,
    )
}
