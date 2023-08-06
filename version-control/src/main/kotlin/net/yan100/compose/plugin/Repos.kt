package net.yan100.compose.plugin

/**
 * maven 仓库管理
 */
object Repos {
  object Credentials {
    val yunXiaoUsername: String = System.getenv("YUNXIAO_USER")
    val yunXiaoPassword: String = System.getenv("YUNXIAO_PWD")
  }

  const val release =
    "https://packages.aliyun.com/maven/repository/2336368-release-CiFRF5/"
  const val snapshot =
    "https://packages.aliyun.com/maven/repository/2336368-snapshot-7SUFMh/"
  private const val repoAli = "https://maven.aliyun.com/"
  const val aliJCenter = "${repoAli}repository/jcenter"
  const val aliCentral = """${repoAli}repository/central"""
  const val aliPublic = "${repoAli}repository/public"
  const val aliGoogle = "${repoAli}repository/google"
  const val aliGradlePlugin = "${repoAli}repository/gradle-plugin"
  const val aliSpring = "${repoAli}repository/spring"
  const val aliSpringPlugin = "${repoAli}repository/spring-plugin"
  const val aliGrailsCore = "${repoAli}repository/grails-core"
  const val aliApacheSnapshots = "${repoAli}repository/apache-snapshots"
  const val huaweiCloudMaven = "https://repo.huaweicloud.com/repository/maven"
  const val springMilestone = "https://repo.spring.io/milestone"
  const val springLibMilestone = "https://repo.spring.io/libs-milestone"
  const val springSnapshot = "https://repo.spring.io/snapshot"
}

