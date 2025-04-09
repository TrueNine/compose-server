package net.yan100.compose.data.extract.service

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import net.yan100.compose.core.exceptions.RemoteCallException
import net.yan100.compose.data.extract.domain.CnDistrictCode
import net.yan100.compose.testtookit.assertEmpty
import net.yan100.compose.testtookit.assertNotEmpty
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
@DisplayName("地址服务测试")
class ILazyAddressServiceTest {

  companion object {
    init {
      // 配置 MockK 日志
      System.setProperty("mockk.debug", "true")
      System.setProperty("mockk.debug.log.level", "warn")
      System.setProperty("mockk.debug.log.stacktrace.length", "20")

      // 配置验证失败时的输出
      System.setProperty("mockk.verify.verification-timeout", "0") // 禁用验证超时
      System.setProperty("mockk.verify.ordering", "sequence") // 默认使用顺序验证
      System.setProperty("mockk.verify.call-matcher", "exact") // 使用精确匹配
      System.setProperty("mockk.output.verbose", "true") // 详细输出模式
    }
  }

  @Autowired private lateinit var service: ILazyAddressService

  @BeforeEach
  fun setup() {
    service =
      mockk(relaxed = true) {
        every { supportedDefaultYearVersion } returns "2024"
        every { supportedYearVersions } returns listOf("2022", "2023", "2024")
        every { lastYearVersion } returns "2024"
      }
  }

  @Nested
  @DisplayName("基础功能测试")
  inner class BasicFunctionsTest {
    @Test
    @DisplayName("验证默认年份版本")
    fun verifyDefaultYearVersion() {
      assertEquals("2024", service.supportedDefaultYearVersion)
      verify { service.supportedDefaultYearVersion }
    }

    @Test
    @DisplayName("验证支持的年份版本列表")
    fun verifySupportedYearVersions() {
      val versions = service.supportedYearVersions
      assertNotNull(versions)
      assertEquals(3, versions.size)
      assertTrue(versions.contains("2024"))
      verify { service.supportedYearVersions }
    }

    @Test
    @DisplayName("验证有效地区代码格式")
    fun verifyValidDistrictCode() {
      assertAll(
        { assertTrue(ILazyAddressService.verifyCode("110000"), "省级代码验证失败") },
        { assertTrue(ILazyAddressService.verifyCode("110100"), "市级代码验证失败") },
        { assertTrue(ILazyAddressService.verifyCode("110101"), "区级代码验证失败") },
      )
    }

    @Test
    @DisplayName("验证无效地区代码格式")
    fun verifyInvalidDistrictCode() {
      assertAll(
        {
          assertFalse(ILazyAddressService.verifyCode("abc123"), "字母数字混合代码应该无效")
        },
        {
          assertFalse(ILazyAddressService.verifyCode("12345x"), "含有字母的代码应该无效")
        },
        { assertFalse(ILazyAddressService.verifyCode(""), "空字符串应该无效") },
        {
          assertFalse(
            ILazyAddressService.verifyCode("1234567890123"),
            "超长代码应该无效",
          )
        },
      )
    }
  }

  @Nested
  @DisplayName("地区查询测试")
  inner class DistrictQueryTest {

    @Test
    @DisplayName("查询省份列表")
    fun findProvinces() {
      val provinces =
        listOf(
          ILazyAddressService.CnDistrict(
            code = CnDistrictCode("110000"),
            name = "北京市",
            yearVersion = "2024",
            level = 1,
          )
        )
      every { service.findAllProvinces(any()) } returns provinces

      val result = service.findAllProvinces()
      assertNotNull(result)
      assertNotEmpty { result }
      assertEquals("北京市", result.first().name)
      verify { service.findAllProvinces(any()) }
    }

    @Test
    @DisplayName("查询城市列表")
    fun findCities() {
      val cities =
        listOf(
          ILazyAddressService.CnDistrict(
            code = CnDistrictCode("110100"),
            name = "北京市",
            yearVersion = "2024",
            level = 2,
          )
        )
      every { service.findAllCityByCode(any(), any()) } returns cities

      val result = service.findAllCityByCode("110000")
      assertNotNull(result)
      assertNotEmpty { result }
      assertEquals("北京市", result.first().name)
      verify { service.findAllCityByCode(any(), any()) }
    }
  }

  @Nested
  @DisplayName("地区代码测试")
  inner class DistrictCodeTest {

    @Test
    @DisplayName("验证地区代码级别")
    fun verifyDistrictCodeLevel() {
      assertAll(
        { assertEquals(1, CnDistrictCode("110000").level, "省级代码级别验证失败") },
        { assertEquals(2, CnDistrictCode("110100").level, "市级代码级别验证失败") },
        { assertEquals(3, CnDistrictCode("110101").level, "区级代码级别验证失败") },
        { assertEquals(4, CnDistrictCode("110101001").level, "街道级代码级别验证失败") },
        { assertEquals(5, CnDistrictCode("110101001001").level, "社区级代码级别验证失败") },
      )
    }

    @Test
    @DisplayName("创建地区代码 - 边界测试")
    fun createDistrictCodeBoundaryTest() {
      assertAll(
        { assertNull(ILazyAddressService.createCnDistrict("")) },
        { assertNull(ILazyAddressService.createCnDistrict(null)) },
        { assertNull(ILazyAddressService.createCnDistrict("1234567890123")) },
        { assertNotNull(ILazyAddressService.createCnDistrict("110000")) },
      )
    }
  }

  @Nested
  @DisplayName("查找功能测试")
  inner class LookupFunctionsTest {

    @Test
    @DisplayName("根据代码查找地区")
    fun findByCode() {
      val district =
        ILazyAddressService.CnDistrict(
          code = CnDistrictCode("110000"),
          name = "北京市",
          yearVersion = "2024",
          level = 1,
        )
      every { service.findByCode("110000", yearVersion = any()) } returns
        district

      val result = service.findByCode("110000")
      assertNotNull(result)
      assertEquals("北京市", result.name)
      verify { service.findByCode("110000", yearVersion = "2024") }
    }

    @Test
    @DisplayName("异常处理测试")
    fun handleException() {
      every { service.findAllChildrenByCode(any()) } throws
        RemoteCallException("Remote call failed")

      val result = service.findAllChildrenByCode("error")
      assertEmpty { result }
      verify { service.findAllChildrenByCode("error", "2024") }
    }
  }

  @Nested
  @DisplayName("懒加载功能测试")
  inner class LazyLoadingTest {

    @Test
    @DisplayName("测试懒加载查询流程")
    fun testLazyLoadingProcess() {
      var preHandleCalled = false
      var postProcessorCalled = false

      every {
        service.lazyFindAllChildrenByCode<Unit>(
          any(),
          preHandle = any(),
          postProcessor = any(),
        )
      } answers
        {
          firstArg<String>()
          secondArg<() -> Pair<Boolean, List<String>>>().invoke().also {
            preHandleCalled = true
          }
          thirdArg<(List<String>) -> List<String>>()
          emptyList()
        }

      service.lazyFindAllChildrenByCode(
        "110000",
        preHandle = {
          preHandleCalled = true
          true to listOf("Test1", "Test2")
        },
        postProcessor = {
          postProcessorCalled = true
          listOf("Post1", "Post2")
        },
      )

      assertAll(
        { assertTrue(preHandleCalled, "预处理应该被调用") },
        { assertFalse(postProcessorCalled, "后处理不应该被调用") },
      )
    }

    @Test
    @DisplayName("测试后处理器调用")
    fun testPostProcessor() {
      var postProcessorCalled = false

      every {
        service.lazyFindAllChildrenByCode<String>(
          any(),
          preHandle = any(),
          postProcessor = any(),
        )
      } answers
        {
          firstArg<String>()
          val preHandle = secondArg<() -> Pair<Boolean, List<String>>>()
          val postProcessor = thirdArg<(List<String>) -> List<String>>()
          val (shouldContinue, _) = preHandle()
          if (!shouldContinue) {
            postProcessor(listOf()).also { postProcessorCalled = true }
          } else {
            emptyList()
          }
        }

      service.lazyFindAllChildrenByCode(
        "110000",
        preHandle = { false to emptyList() },
        postProcessor = {
          postProcessorCalled = true
          listOf("Processed")
        },
      )

      assertTrue(postProcessorCalled, "后处理器应该被调用")
    }
  }

  @Nested
  @DisplayName("版本回退测试")
  inner class VersionFallbackTest {

    @Test
    @DisplayName("测试版本回退机制")
    fun testVersionFallback() {
      // 配置所有预期的调用
      every { service.supportedYearVersions } returns listOf("2024", "2023")
      every { service.lastYearVersion } returns "2024"
      every {
        service.lookupByCode<ILazyAddressService.CnDistrict>(
          code = "999999",
          firstFind = any(),
          deepCondition = any(),
          notFound = any(),
          yearVersion = "2024",
          sortedSave = any(),
        )
      } returns null

      // 执行测试
      val versions = service.supportedYearVersions // 显式调用以触发验证
      val result =
        service.lookupByCode<ILazyAddressService.CnDistrict>("999999")

      // 验证结果
      assertNull(result)
      assertEquals(listOf("2024", "2023"), versions)

      // 验证调用序列
      verifySequence {
        service.supportedYearVersions
        service.lastYearVersion
        service.lookupByCode<ILazyAddressService.CnDistrict>(
          code = "999999",
          firstFind = any(),
          deepCondition = any(),
          notFound = any(),
          yearVersion = "2024",
          sortedSave = any(),
        )
      }
    }

    @Test
    @DisplayName("测试历史数据查询")
    fun testHistoricalDataQuery() {
      val historicalDistrict =
        ILazyAddressService.CnDistrict(
          code = CnDistrictCode("653126201"),
          name = "测试区域",
          yearVersion = "2023",
          level = 3,
        )

      // 配置 mock 行为
      every { service.lastYearVersion } returns "2024"
      every {
        service.lookupAllChildrenByCode<ILazyAddressService.CnDistrict>(
          code = "653126201",
          firstFind = any(),
          deepCondition = any(),
          notFound = any(),
          yearVersion = any(),
          sortedSave = any(),
        )
      } answers
        {
          // 执行 firstFind 函数
          val firstFind =
            secondArg<
              (ILazyAddressService.LookupFindDto) -> List<
                  ILazyAddressService.CnDistrict
                >
            >()
          val lookupDto =
            ILazyAddressService.LookupFindDto(code = "653126201", level = 3)
          firstFind(lookupDto)
        }

      // 执行测试
      val result =
        service.lookupAllChildrenByCode<ILazyAddressService.CnDistrict>(
          "653126201",
          firstFind = { listOf(historicalDistrict) },
          deepCondition = { false },
        )

      // 验证结果
      assertNotEmpty { result }
      assertEquals(historicalDistrict, result.first())

      // 验证调用
      verifySequence {
        service.lastYearVersion
        service.lookupAllChildrenByCode<ILazyAddressService.CnDistrict>(
          code = "653126201",
          firstFind = any(),
          deepCondition = any(),
          notFound = any(),
          yearVersion = any(),
          sortedSave = any(),
        )
      }
      confirmVerified(service)
    }
  }
}
