package io.github.truenine.composeserver.annotations

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * # 敏感响应注解测试
 *
 * 测试 SensitiveResponse 注解的属性和行为
 */
class SensitiveResponseTest {

  @SensitiveResponse
  fun testMethodWithSensitiveResponse(): String {
    return "sensitive data"
  }

  fun testMethodWithoutSensitiveResponse(): String {
    return "normal data"
  }

  @Test
  fun `测试 SensitiveResponse 注解的存在性`() {
    log.info("测试 SensitiveResponse 注解的存在性")

    val method = this::class.java.getDeclaredMethod("testMethodWithSensitiveResponse")
    val annotation = method.getAnnotation(SensitiveResponse::class.java)

    assertNotNull(annotation, "方法应该有 SensitiveResponse 注解")
    log.info("找到 SensitiveResponse 注解: {}", annotation)
  }

  @Test
  fun `测试没有 SensitiveResponse 注解的方法`() {
    log.info("测试没有 SensitiveResponse 注解的方法")

    val method = this::class.java.getDeclaredMethod("testMethodWithoutSensitiveResponse")
    val annotation = method.getAnnotation(SensitiveResponse::class.java)

    assertEquals(null, annotation, "方法不应该有 SensitiveResponse 注解")
    log.info("确认方法没有 SensitiveResponse 注解")
  }

  @Test
  fun `测试 SensitiveResponse 注解的元注解`() {
    log.info("测试 SensitiveResponse 注解的元注解")

    val annotationClass = SensitiveResponse::class.java
    val annotations = annotationClass.annotations

    log.info("SensitiveResponse 注解的元注解数量: {}", annotations.size)

    annotations.forEach { annotation -> log.info("元注解: {}", annotation.annotationClass.simpleName) }

    // 验证包含预期的元注解
    val annotationNames = annotations.map { it.annotationClass.simpleName }.toSet()
    assertTrue(annotationNames.contains("Inherited"), "应该包含 @Inherited 注解")
    assertTrue(annotationNames.contains("Retention"), "应该包含 @Retention 注解")
    assertTrue(annotationNames.contains("MustBeDocumented"), "应该包含 @MustBeDocumented 注解")
    assertTrue(annotationNames.contains("Target"), "应该包含 @Target 注解")
  }

  @Test
  fun `测试 SensitiveResponse 注解的 Target 设置`() {
    log.info("测试 SensitiveResponse 注解的 Target 设置")

    val annotationClass = SensitiveResponse::class.java
    val targetAnnotation = annotationClass.getAnnotation(Target::class.java)

    assertNotNull(targetAnnotation, "应该有 @Target 注解")

    val allowedTargets = targetAnnotation.allowedTargets
    log.info("允许的目标数量: {}", allowedTargets.size)

    allowedTargets.forEach { target -> log.info("允许的目标: {}", target.name) }

    assertTrue(allowedTargets.contains(AnnotationTarget.FUNCTION), "应该允许用于函数")
  }

  @Test
  fun `测试 SensitiveResponse 注解的 Retention 设置`() {
    log.info("测试 SensitiveResponse 注解的 Retention 设置")

    val annotationClass = SensitiveResponse::class.java
    val retentionAnnotation = annotationClass.getAnnotation(Retention::class.java)

    assertNotNull(retentionAnnotation, "应该有 @Retention 注解")

    val retentionPolicy = retentionAnnotation.value
    log.info("保留策略: {}", retentionPolicy.name)

    // 通常应该是 RUNTIME，以便在运行时可以访问
    assertEquals(AnnotationRetention.RUNTIME, retentionPolicy, "应该使用 RUNTIME 保留策略")
  }

  @Test
  fun `测试 SensitiveResponse 注解的继承性`() {
    log.info("测试 SensitiveResponse 注解的继承性")

    val annotationClass = SensitiveResponse::class.java
    val inheritedAnnotation = annotationClass.getAnnotation(java.lang.annotation.Inherited::class.java)

    assertNotNull(inheritedAnnotation, "应该有 @Inherited 注解")
    log.info("确认 SensitiveResponse 注解支持继承")
  }

  @Test
  fun `测试 SensitiveResponse 注解的文档化`() {
    log.info("测试 SensitiveResponse 注解的文档化")

    val annotationClass = SensitiveResponse::class.java
    val documentedAnnotation = annotationClass.getAnnotation(MustBeDocumented::class.java)

    assertNotNull(documentedAnnotation, "应该有 @MustBeDocumented 注解")
    log.info("确认 SensitiveResponse 注解会被文档化")
  }

  @Test
  fun `测试 SensitiveResponse 注解在反射中的可见性`() {
    log.info("测试 SensitiveResponse 注解在反射中的可见性")

    val method = this::class.java.getDeclaredMethod("testMethodWithSensitiveResponse")
    val annotations = method.annotations

    log.info("方法上的注解数量: {}", annotations.size)

    val sensitiveResponseAnnotations = annotations.filterIsInstance<SensitiveResponse>()
    assertEquals(1, sensitiveResponseAnnotations.size, "应该找到一个 SensitiveResponse 注解")

    log.info("在反射中成功找到 SensitiveResponse 注解")
  }

  @Test
  fun `测试 SensitiveResponse 注解的类型信息`() {
    log.info("测试 SensitiveResponse 注解的类型信息")

    val annotationClass = SensitiveResponse::class.java

    assertTrue(annotationClass.isAnnotation, "SensitiveResponse 应该是注解类型")
    assertEquals("SensitiveResponse", annotationClass.simpleName, "注解名称应该正确")
    assertEquals("io.github.truenine.composeserver.annotations", annotationClass.packageName, "包名应该正确")

    log.info("注解类型信息验证通过")
  }

  // 测试继承场景
  open class BaseClass {
    @SensitiveResponse open fun sensitiveMethod(): String = "base sensitive"
  }

  class DerivedClass : BaseClass() {
    override fun sensitiveMethod(): String = "derived sensitive"
  }

  @Test
  fun `测试 SensitiveResponse 注解的继承行为`() {
    log.info("测试 SensitiveResponse 注解的继承行为")

    val baseMethod = BaseClass::class.java.getDeclaredMethod("sensitiveMethod")
    val derivedMethod = DerivedClass::class.java.getDeclaredMethod("sensitiveMethod")

    val baseAnnotation = baseMethod.getAnnotation(SensitiveResponse::class.java)
    val derivedAnnotation = derivedMethod.getAnnotation(SensitiveResponse::class.java)

    assertNotNull(baseAnnotation, "基类方法应该有 SensitiveResponse 注解")

    // 注意：Java 的 @Inherited 只对类级别的注解有效，对方法级别的注解无效
    // 所以派生类的重写方法不会自动继承注解
    log.info("基类方法注解: {}", baseAnnotation)
    log.info("派生类方法注解: {}", derivedAnnotation)
  }
}
