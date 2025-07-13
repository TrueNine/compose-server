package io.github.truenine.composeserver.security.controller

import io.github.truenine.composeserver.Pr
import io.github.truenine.composeserver.annotations.SensitiveResponse
import io.github.truenine.composeserver.domain.ISensitivity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** 扩展的敏感数据测试控制器 用于测试更多的敏感数据处理场景 */
@RestController
@RequestMapping("test/sensitive/extended")
class ExtendedSensitiveController {

  /** 用户信息响应类，包含多种敏感数据 */
  class UserInfo(
    var id: Long = 1L,
    var username: String = "testuser",
    var phone: String = "13812345678",
    var email: String = "test@example.com",
    var idCard: String = "110101199001011234",
    var address: String = "北京市朝阳区建国门外大街1号",
  ) : ISensitivity {
    override fun changeWithSensitiveData() {
      super.changeWithSensitiveData()
      // 脱敏处理
      phone = "138****5678"
      email = "te****@example.com"
      idCard = "11****1234"
      address = "北京市****1号"
    }
  }

  /** 简单的敏感数据类 */
  class SimpleData(var value: String = "sensitive") : ISensitivity {
    override fun changeWithSensitiveData() {
      super.changeWithSensitiveData()
      value = "****"
    }
  }

  /** 嵌套的敏感数据类 */
  class NestedData(var publicInfo: String = "public", var sensitiveInfo: SimpleData = SimpleData()) : ISensitivity {
    override fun changeWithSensitiveData() {
      super.changeWithSensitiveData()
      sensitiveInfo.changeWithSensitiveData()
    }
  }

  @SensitiveResponse
  @GetMapping("user", produces = ["application/json"])
  fun getUserInfo(): Pr<UserInfo> {
    return Pr[
      listOf(
        UserInfo(1L, "user1", "13812345678", "user1@test.com", "110101199001011234", "北京市朝阳区建国门外大街1号"),
        UserInfo(2L, "user2", "18612345678", "user2@test.com", "110101199001011235", "上海市浦东新区陆家嘴环路1000号"),
      )]
  }

  @SensitiveResponse
  @GetMapping("simple", produces = ["application/json"])
  fun getSimpleData(): SimpleData {
    return SimpleData("very sensitive data")
  }

  @SensitiveResponse
  @GetMapping("nested", produces = ["application/json"])
  fun getNestedData(): NestedData {
    return NestedData("public info", SimpleData("nested sensitive"))
  }

  @SensitiveResponse
  @GetMapping("collection", produces = ["application/json"])
  fun getCollection(): Collection<SimpleData> {
    return listOf(SimpleData("data1"), SimpleData("data2"), SimpleData("data3"))
  }

  @SensitiveResponse
  @GetMapping("array", produces = ["application/json"])
  fun getArray(): Array<SimpleData> {
    return arrayOf(SimpleData("array1"), SimpleData("array2"))
  }

  @SensitiveResponse
  @GetMapping("map", produces = ["application/json"])
  fun getMap(): Map<String, SimpleData> {
    return mapOf("key1" to SimpleData("map1"), "key2" to SimpleData("map2"))
  }

  /** 不带 @SensitiveResponse 注解的方法，用于对比测试 */
  @GetMapping("no-annotation", produces = ["application/json"])
  fun getDataWithoutAnnotation(): SimpleData {
    return SimpleData("should not be desensitized")
  }
}
