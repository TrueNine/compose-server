package io.github.truenine.composeserver.gradleplugin.generator

import org.junit.jupiter.api.Test
import kotlin.test.*

class MavenRepoTypeTest {

  @Test
  fun `ALIYUN should have correct URLs`() {
    val aliyun = MavenRepoType.ALIYUN

    assertNotNull(aliyun.mavenCentralUrl)
    assertNotNull(aliyun.gradlePluginUrl)
    assertNotNull(aliyun.googlePluginUrl)
    assertNotNull(aliyun.jCenterUrl)
    assertNull(aliyun.gradleDistributionUrl)

    assertEquals("https://maven.aliyun.com/repository/central", aliyun.mavenCentralUrl)
    assertEquals("https://maven.aliyun.com/repository/gradle-plugin", aliyun.gradlePluginUrl)
    assertEquals("https://maven.aliyun.com/repository/google", aliyun.googlePluginUrl)
    assertEquals("https://maven.aliyun.com/repository/jcenter", aliyun.jCenterUrl)
  }

  @Test
  fun `TENCENT_CLOUD should have correct URLs`() {
    val tencentCloud = MavenRepoType.TENCENT_CLOUD

    assertNotNull(tencentCloud.mavenCentralUrl)
    assertNotNull(tencentCloud.googlePluginUrl)
    assertNotNull(tencentCloud.gradlePluginUrl)
    assertNull(tencentCloud.jCenterUrl)
    assertNull(tencentCloud.gradleDistributionUrl)

    assertEquals("https://mirrors.cloud.tencent.com/repository/maven-public/", tencentCloud.mavenCentralUrl)
    assertEquals("https://mirrors.cloud.tencent.com/repository/maven-public/", tencentCloud.googlePluginUrl)
    assertEquals("https://maven.aliyun.com/repository/gradle-plugin", tencentCloud.gradlePluginUrl)
  }

  @Test
  fun `HUAWEI_CLOUD should have correct URLs`() {
    val huaweiCloud = MavenRepoType.HUAWEI_CLOUD

    assertNotNull(huaweiCloud.mavenCentralUrl)
    assertNull(huaweiCloud.googlePluginUrl)
    assertNull(huaweiCloud.jCenterUrl)
    assertNull(huaweiCloud.gradlePluginUrl)
    assertNull(huaweiCloud.gradleDistributionUrl)

    assertEquals("https://repo.huaweicloud.com/repository/maven", huaweiCloud.mavenCentralUrl)
  }

  @Test
  fun `DEFAULT should have all null URLs`() {
    val default = MavenRepoType.DEFAULT

    assertNull(default.mavenCentralUrl)
    assertNull(default.googlePluginUrl)
    assertNull(default.jCenterUrl)
    assertNull(default.gradlePluginUrl)
    assertNull(default.gradleDistributionUrl)
  }

  @Test
  fun `have_all_enum_values`() {
    val values = MavenRepoType.values()

    assertEquals(4, values.size)
    assertEquals(MavenRepoType.ALIYUN, values[0])
    assertEquals(MavenRepoType.TENCENT_CLOUD, values[1])
    assertEquals(MavenRepoType.HUAWEI_CLOUD, values[2])
    assertEquals(MavenRepoType.DEFAULT, values[3])
  }

  @Test
  fun `support_value_of`() {
    assertEquals(MavenRepoType.ALIYUN, MavenRepoType.valueOf("ALIYUN"))
    assertEquals(MavenRepoType.TENCENT_CLOUD, MavenRepoType.valueOf("TENCENT_CLOUD"))
    assertEquals(MavenRepoType.HUAWEI_CLOUD, MavenRepoType.valueOf("HUAWEI_CLOUD"))
    assertEquals(MavenRepoType.DEFAULT, MavenRepoType.valueOf("DEFAULT"))
  }
}
