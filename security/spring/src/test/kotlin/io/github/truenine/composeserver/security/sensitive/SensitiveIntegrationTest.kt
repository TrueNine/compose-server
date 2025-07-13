package io.github.truenine.composeserver.security.sensitive

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.Pr
import io.github.truenine.composeserver.annotations.SensitiveResponse
import io.github.truenine.composeserver.domain.ISensitivity
import io.github.truenine.composeserver.security.autoconfig.SensitiveResultResponseBodyAdvice
import io.github.truenine.composeserver.testtoolkit.log
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * 敏感数据处理集成测试
 * 测试完整的敏感数据处理流程
 */
class SensitiveIntegrationTest {

  private lateinit var mockMvc: MockMvc
  private val objectMapper = ObjectMapper()

  /**
   * 模拟真实的用户数据类
   */
  class RealUserData(
    var id: Long = 1L,
    var username: String = "realuser",
    var realName: String = "张三",
    var phone: String = "13812345678",
    var email: String = "zhangsan@company.com",
    var idCard: String = "110101199001011234",
    var bankCard: String = "6212341234123434",
    var address: String = "北京市朝阳区建国门外大街1号国贸大厦",
    var password: String = "MySecretPassword123!",
    var salary: Double = 50000.0,
    var isVip: Boolean = true
  ) : ISensitivity {
    override fun changeWithSensitiveData() {
      super.changeWithSensitiveData()
      // 模拟真实的脱敏处理 - 保留最后一个字符
      realName = if (realName.isNotEmpty()) "**${realName.last()}" else "**"
      phone = "138****5678"
      email = "zh****@company.com"
      idCard = "11****1234"
      bankCard = "62****3434"
      address = "北京市朝阳区****大厦"
      password = "****"
      salary = 0.0 // 薪资完全隐藏
    }
  }

  /**
   * 模拟业务控制器
   */
  @RestController
  @RequestMapping("api/users")
  class UserController {

    @SensitiveResponse
    @GetMapping("profile", produces = ["application/json"])
    fun getUserProfile(): RealUserData {
      return RealUserData(
        id = 12345L,
        username = "john_doe",
        realName = "约翰·多伊",
        phone = "18612345678",
        email = "john.doe@example.com",
        idCard = "440301199001011234",
        bankCard = "4367123456781278",
        address = "广东省深圳市南山区科技园南区深南大道10000号",
        password = "SuperSecretPassword!@#",
        salary = 80000.0,
        isVip = true
      )
    }

    @SensitiveResponse
    @GetMapping("list", produces = ["application/json"])
    fun getUserList(): Pr<RealUserData> {
      return Pr[listOf(
        RealUserData(1L, "user1", "李明", "13812345678", "liming@test.com", "110101199001011234", "6212341234123434", "北京市海淀区中关村大街1号", "password123", 45000.0, false),
        RealUserData(2L, "user2", "王芳", "18612345678", "wangfang@test.com", "310101199001011235", "4367123456781278", "上海市浦东新区陆家嘴环路1000号", "mypassword", 55000.0, true),
        RealUserData(3L, "user3", "刘强", "15912345678", "liuqiang@test.com", "440301199001011236", "5212341234123434", "广东省深圳市福田区深南大道2000号", "secret123", 60000.0, true)
      )]
    }

    @GetMapping("public", produces = ["application/json"])
    fun getPublicUserInfo(): RealUserData {
      return RealUserData(
        id = 999L,
        username = "public_user",
        realName = "公开用户",
        phone = "13800138000",
        email = "public@example.com",
        idCard = "000000000000000000",
        bankCard = "0000000000000000",
        address = "公开地址",
        password = "publicpassword",
        salary = 0.0,
        isVip = false
      )
    }
  }

  @BeforeEach
  fun setup() {
    val controller = UserController()
    val sensitive = SensitiveResultResponseBodyAdvice()
    mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(sensitive).build()
  }

  @Test
  fun `test complete user profile desensitization flow`() {
    log.info("测试完整的用户资料脱敏流程")
    
    val result = mockMvc.get("/api/users/profile").andExpect {
      status { isOk() }
      content { contentType(MediaType.APPLICATION_JSON) }
    }.andReturn()
    
    val responseContent = result.response.contentAsString
    log.info("用户资料响应: {}", responseContent)
    
    val userData = objectMapper.readValue(responseContent, Map::class.java)
    
    // 验证非敏感数据保持不变
    assertEquals(12345, userData["id"])
    assertEquals("john_doe", userData["username"])
    assertEquals(true, userData["isVip"])
    
    // 验证敏感数据被正确脱敏
    assertEquals("**伊", userData["realName"], "真实姓名应该被脱敏")
    assertEquals("138****5678", userData["phone"], "手机号应该被脱敏")
    assertEquals("zh****@company.com", userData["email"], "邮箱应该被脱敏")
    assertEquals("11****1234", userData["idCard"], "身份证号应该被脱敏")
    assertEquals("62****3434", userData["bankCard"], "银行卡号应该被脱敏")
    assertTrue((userData["address"] as String).contains("****"), "地址应该被脱敏")
    assertEquals("****", userData["password"], "密码应该被完全隐藏")
    assertEquals(0.0, userData["salary"], "薪资应该被隐藏")
    
    log.info("用户资料脱敏流程测试通过")
  }

  @Test
  fun `test user list desensitization with pagination`() {
    log.info("测试用户列表脱敏（带分页）")
    
    val result = mockMvc.get("/api/users/list").andExpect {
      status { isOk() }
      content { contentType(MediaType.APPLICATION_JSON) }
    }.andReturn()
    
    val responseContent = result.response.contentAsString
    log.info("用户列表响应: {}", responseContent)
    
    val responseMap = objectMapper.readValue(responseContent, Map::class.java)
    val dataList = responseMap["d"] as List<*>
    val total = responseMap["t"] as Int
    val pageSize = responseMap["p"] as Int
    
    // 验证分页信息
    assertEquals(3, total, "总数应该正确")
    assertEquals(1, pageSize, "页数应该正确")
    assertEquals(3, dataList.size, "数据列表大小应该正确")
    
    // 验证每个用户的敏感数据都被脱敏
    dataList.forEach { item ->
      val userMap = item as Map<*, *>
      val realName = userMap["realName"] as String
      val phone = userMap["phone"] as String
      val email = userMap["email"] as String
      val password = userMap["password"] as String
      val salary = userMap["salary"] as Double
      
      assertTrue(realName.startsWith("**"), "真实姓名应该被脱敏: $realName")
      assertTrue(phone.contains("****"), "手机号应该被脱敏: $phone")
      assertTrue(email.contains("****"), "邮箱应该被脱敏: $email")
      assertEquals("****", password, "密码应该被完全隐藏")
      assertEquals(0.0, salary, "薪资应该被隐藏")
    }
    
    log.info("用户列表脱敏测试通过")
  }

  @Test
  fun `test public endpoint without sensitive annotation`() {
    log.info("测试没有敏感注解的公开接口")
    
    val result = mockMvc.get("/api/users/public").andExpect {
      status { isOk() }
      content { contentType(MediaType.APPLICATION_JSON) }
    }.andReturn()
    
    val responseContent = result.response.contentAsString
    log.info("公开接口响应: {}", responseContent)
    
    val userData = objectMapper.readValue(responseContent, Map::class.java)
    
    // 验证没有注解的接口不会进行脱敏处理
    assertEquals("公开用户", userData["realName"], "没有注解的接口不应该脱敏真实姓名")
    assertEquals("13800138000", userData["phone"], "没有注解的接口不应该脱敏手机号")
    assertEquals("public@example.com", userData["email"], "没有注解的接口不应该脱敏邮箱")
    assertEquals("publicpassword", userData["password"], "没有注解的接口不应该脱敏密码")
    assertEquals(0.0, userData["salary"], "原始薪资数据应该保持不变")
    
    log.info("公开接口测试通过")
  }

  @Test
  fun `test ISensitivity interface state management`() {
    log.info("测试 ISensitivity 接口状态管理")
    
    val userData = RealUserData()
    val originalPhone = userData.phone
    val originalEmail = userData.email
    val originalSalary = userData.salary
    
    // 验证初始状态
    assertFalse(userData.isChangedToSensitiveData, "初始状态应该未脱敏")
    
    // 执行脱敏
    userData.changeWithSensitiveData()
    
    // 验证脱敏后的状态
    assertTrue(userData.phone != originalPhone, "手机号应该被脱敏")
    assertTrue(userData.email != originalEmail, "邮箱应该被脱敏")
    assertTrue(userData.salary != originalSalary, "薪资应该被脱敏")
    assertEquals("****", userData.password, "密码应该被完全隐藏")
    
    // 验证可以重复调用脱敏方法而不会出错
    val phoneAfterFirstCall = userData.phone
    userData.changeWithSensitiveData()
    assertEquals(phoneAfterFirstCall, userData.phone, "重复调用脱敏方法应该保持一致")
    
    log.info("ISensitivity 接口状态管理测试通过")
  }
}
