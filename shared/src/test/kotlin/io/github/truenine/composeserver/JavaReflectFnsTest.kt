package io.github.truenine.composeserver

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Validates reflection extension functions defined in JavaReflectFns.kt.
 */
class JavaReflectFnsTest {

  // Test class hierarchy
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
  fun collectsFieldsAcrossInheritance() {
    val fields = DerivedClass::class.recursionFields()

    log.info("Retrieved field count: {}", fields.size)
    fields.forEach { field -> log.info("Field: {}, type: {}, declared in: {}", field.name, field.type, field.declaringClass.simpleName) }

    // Ensure fields from every level are included
    val fieldNames = fields.map { it.name }.toSet()
    assertTrue(fieldNames.contains("derivedField"), "Should include derived-class fields")
    assertTrue(fieldNames.contains("internalDerivedField"), "Should include internal derived-class fields")
    assertTrue(fieldNames.contains("middleField"), "Should include middle-class fields")
    assertTrue(fieldNames.contains("protectedMiddleField"), "Should include protected middle-class fields")
    assertTrue(fieldNames.contains("baseField"), "Should include base-class fields")
    assertTrue(fieldNames.contains("privateBaseField"), "Should include private base-class fields")
  }

  @Test
  fun collectsFieldsUpToSpecifiedEndType() {
    val fields = DerivedClass::class.recursionFields(endType = BaseClass::class)

    log.info("Field count with end type specified: {}", fields.size)
    fields.forEach { field -> log.info("Field: {}, declared in: {}", field.name, field.declaringClass.simpleName) }

    val fieldNames = fields.map { it.name }.toSet()
    assertTrue(fieldNames.contains("derivedField"), "Should include derived-class fields")
    assertTrue(fieldNames.contains("middleField"), "Should include middle-class fields")
    // When an end type is specified, its fields are not included because the traversal stops before processing that class
  }

  @Test
  fun collectsFieldsFromSingleClass() {
    class SingleClass {
      val singleField: String = "single"
      private val privateSingleField: Int = 1
    }

    val fields = SingleClass::class.recursionFields()

    log.info("Single-class field count: {}", fields.size)

    val fieldNames = fields.map { it.name }.toSet()
    assertTrue(fieldNames.contains("singleField"), "Should include public fields")
    assertTrue(fieldNames.contains("privateSingleField"), "Should include private fields")
  }

  @Test
  fun handlesEmptyClass() {
    class EmptyClass

    val fields = EmptyClass::class.recursionFields()

    log.info("Empty class field count: {}", fields.size)

    // Empty classes should have no user-defined fields, though synthetic fields are possible
    assertTrue(fields.isEmpty() || fields.all { it.isSynthetic }, "Empty class should not expose user-defined fields")
  }

  @Test
  fun inspectsFieldAccessibility() {
    val fields = DerivedClass::class.recursionFields()

    // Ensure we can inspect fields with different visibility modifiers
    val publicFields = fields.filter { java.lang.reflect.Modifier.isPublic(it.modifiers) }
    val privateFields = fields.filter { java.lang.reflect.Modifier.isPrivate(it.modifiers) }
    val protectedFields = fields.filter { java.lang.reflect.Modifier.isProtected(it.modifiers) }

    log.info("Public field count: {}", publicFields.size)
    log.info("Private field count: {}", privateFields.size)
    log.info("Protected field count: {}", protectedFields.size)
    log.info("All fields: {}", fields.map { "${it.name}(${it.modifiers})" })

    assertTrue(fields.isNotEmpty(), "Should retrieve fields")
    assertTrue(privateFields.isNotEmpty(), "Should include private fields (Kotlin fields are typically private)")
  }
}
