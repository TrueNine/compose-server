package io.github.truenine.composeserver

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * # Java 反射扩展函数测试
 *
 * 测试 JavaReflectFns.kt 中定义的反射相关扩展函数
 */
class JavaReflectFnsTest {

  // 测试用的类层次结构
  open class BaseClass {
    val baseField: String = "base"
    private val privateBaseField: Int = 1
  }

  open class MiddleClass : BaseClass() {
    val middleField: Double = 2.0
    protected val protectedMiddleField: Boolean = true
  }

  class DerivedClass : MiddleClass() {
    val derivedField: Long = 3L
    internal val internalDerivedField: Float = 4.0f
  }

  @Test
  fun `测试 recursionFields 方法 - 获取类的所有字段包括继承的字段`() {
    val fields = DerivedClass::class.recursionFields()

    log.info("获取到的字段数量: {}", fields.size)
    fields.forEach { field -> log.info("字段名: {}, 类型: {}, 声明类: {}", field.name, field.type, field.declaringClass.simpleName) }

    // 验证包含所有层级的字段
    val fieldNames = fields.map { it.name }.toSet()
    assertTrue(fieldNames.contains("derivedField"), "应该包含派生类的字段")
    assertTrue(fieldNames.contains("internalDerivedField"), "应该包含派生类的内部字段")
    assertTrue(fieldNames.contains("middleField"), "应该包含中间类的字段")
    assertTrue(fieldNames.contains("protectedMiddleField"), "应该包含中间类的受保护字段")
    assertTrue(fieldNames.contains("baseField"), "应该包含基类的字段")
    assertTrue(fieldNames.contains("privateBaseField"), "应该包含基类的私有字段")
  }

  @Test
  fun `测试 recursionFields 方法 - 指定结束类型`() {
    val fields = DerivedClass::class.recursionFields(endType = BaseClass::class)

    log.info("指定结束类型后获取到的字段数量: {}", fields.size)
    fields.forEach { field -> log.info("字段名: {}, 声明类: {}", field.name, field.declaringClass.simpleName) }

    val fieldNames = fields.map { it.name }.toSet()
    assertTrue(fieldNames.contains("derivedField"), "应该包含派生类的字段")
    assertTrue(fieldNames.contains("middleField"), "应该包含中间类的字段")
    // 根据实际实现，当指定结束类型时，不会包含结束类型本身的字段
    // 这是因为循环在 superClass == endsWith 时会 break，不会处理 endsWith 类的字段
  }

  @Test
  fun `测试 recursionFields 方法 - 单个类无继承`() {
    class SingleClass {
      val singleField: String = "single"
      private val privateSingleField: Int = 1
    }

    val fields = SingleClass::class.recursionFields()

    log.info("单个类的字段数量: {}", fields.size)

    val fieldNames = fields.map { it.name }.toSet()
    assertTrue(fieldNames.contains("singleField"), "应该包含类的公共字段")
    assertTrue(fieldNames.contains("privateSingleField"), "应该包含类的私有字段")
  }

  @Test
  fun `测试 recursionFields 方法 - 空类`() {
    class EmptyClass

    val fields = EmptyClass::class.recursionFields()

    log.info("空类的字段数量: {}", fields.size)

    // 空类应该没有自定义字段，但可能有编译器生成的字段
    assertTrue(fields.isEmpty() || fields.all { it.isSynthetic }, "空类应该没有用户定义的字段")
  }

  @Test
  fun `测试 recursionFields 方法 - 验证字段访问性`() {
    val fields = DerivedClass::class.recursionFields()

    // 验证可以获取到不同访问级别的字段
    val publicFields = fields.filter { java.lang.reflect.Modifier.isPublic(it.modifiers) }
    val privateFields = fields.filter { java.lang.reflect.Modifier.isPrivate(it.modifiers) }
    val protectedFields = fields.filter { java.lang.reflect.Modifier.isProtected(it.modifiers) }

    log.info("公共字段数量: {}", publicFields.size)
    log.info("私有字段数量: {}", privateFields.size)
    log.info("受保护字段数量: {}", protectedFields.size)
    log.info("所有字段: {}", fields.map { "${it.name}(${it.modifiers})" })

    // Kotlin 的字段访问性与 Java 不同，大多数字段都是 private 的，通过 getter/setter 访问
    // 所以我们主要验证能获取到字段即可
    assertTrue(fields.isNotEmpty(), "应该能获取到字段")
    assertTrue(privateFields.isNotEmpty(), "应该有私有字段（Kotlin 字段通常是私有的）")
  }
}
