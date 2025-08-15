package io.github.truenine.composeserver.ide.ideamcp.testutil

import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeMetadata
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeResult
import io.github.truenine.composeserver.ide.ideamcp.tools.SourceType
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import kotlin.random.Random

/**
 * 模拟数据生成器
 *
 * 提供工具方法来生成测试用的模拟数据：
 * - 模拟JAR文件
 * - 模拟源码文件
 * - 模拟LibCodeResult对象
 * - 测试用的类定义
 */
object MockDataGenerator {

  /** 生成模拟的Java源码 */
  fun generateMockJavaSource(
    packageName: String = "com.example",
    className: String = "TestClass",
    includeMembers: List<String> = listOf("testMethod", "getValue", "setValue"),
  ): String {
    return buildString {
      appendLine("package $packageName;")
      appendLine()
      appendLine("import java.util.*;")
      appendLine("import java.io.*;")
      appendLine()
      appendLine("/**")
      appendLine(" * 模拟生成的测试类")
      appendLine(" * 用于 LibCodeService 功能测试")
      appendLine(" */")
      appendLine("public class $className {")
      appendLine()

      // 生成字段
      appendLine("    private String value;")
      appendLine("    private int count;")
      appendLine("    private List<String> items;")
      appendLine()

      // 生成构造函数
      appendLine("    public $className() {")
      appendLine("        this.value = \"default\";")
      appendLine("        this.count = 0;")
      appendLine("        this.items = new ArrayList<>();")
      appendLine("    }")
      appendLine()

      appendLine("    public $className(String value) {")
      appendLine("        this.value = value;")
      appendLine("        this.count = 1;")
      appendLine("        this.items = new ArrayList<>();")
      appendLine("    }")
      appendLine()

      // 生成指定的成员方法
      includeMembers.forEach { memberName ->
        when (memberName) {
          "testMethod" -> {
            appendLine("    /**")
            appendLine("     * 测试方法")
            appendLine("     * @param input 输入参数")
            appendLine("     * @return 处理结果")
            appendLine("     */")
            appendLine("    public String testMethod(String input) {")
            appendLine("        if (input == null) {")
            appendLine("            return \"null input\";")
            appendLine("        }")
            appendLine("        return \"processed: \" + input;")
            appendLine("    }")
            appendLine()
          }
          "getValue" -> {
            appendLine("    public String getValue() {")
            appendLine("        return this.value;")
            appendLine("    }")
            appendLine()
          }
          "setValue" -> {
            appendLine("    public void setValue(String value) {")
            appendLine("        this.value = value;")
            appendLine("        this.count++;")
            appendLine("    }")
            appendLine()
          }
          else -> {
            appendLine("    public void $memberName() {")
            appendLine("        // 自动生成的方法: $memberName")
            appendLine("        System.out.println(\"Executing $memberName\");")
            appendLine("    }")
            appendLine()
          }
        }
      }

      // 生成toString方法
      appendLine("    @Override")
      appendLine("    public String toString() {")
      appendLine("        return \"$className{\" +")
      appendLine("                \"value='\" + value + '\\'' +")
      appendLine("                \", count=\" + count +")
      appendLine("                \", items=\" + items +")
      appendLine("                '}';")
      appendLine("    }")
      appendLine()

      // 生成equals和hashCode
      appendLine("    @Override")
      appendLine("    public boolean equals(Object obj) {")
      appendLine("        if (this == obj) return true;")
      appendLine("        if (obj == null || getClass() != obj.getClass()) return false;")
      appendLine("        $className that = ($className) obj;")
      appendLine("        return count == that.count && Objects.equals(value, that.value);")
      appendLine("    }")
      appendLine()

      appendLine("    @Override")
      appendLine("    public int hashCode() {")
      appendLine("        return Objects.hash(value, count);")
      appendLine("    }")

      appendLine("}")
    }
  }

  /** 生成模拟的Kotlin源码 */
  fun generateMockKotlinSource(
    packageName: String = "com.example",
    className: String = "TestClass",
    includeMembers: List<String> = listOf("testMethod", "getValue", "setValue"),
  ): String {
    return buildString {
      appendLine("package $packageName")
      appendLine()
      appendLine("import java.util.*")
      appendLine()
      appendLine("/**")
      appendLine(" * 模拟生成的Kotlin测试类")
      appendLine(" * 用于 LibCodeService 功能测试")
      appendLine(" */")
      appendLine("data class $className(")
      appendLine("    var value: String = \"default\",")
      appendLine("    var count: Int = 0,")
      appendLine("    val items: MutableList<String> = mutableListOf()")
      appendLine(") {")
      appendLine()

      // 生成指定的成员方法
      includeMembers.forEach { memberName ->
        when (memberName) {
          "testMethod" -> {
            appendLine("    /**")
            appendLine("     * 测试方法")
            appendLine("     * @param input 输入参数")
            appendLine("     * @return 处理结果")
            appendLine("     */")
            appendLine("    fun testMethod(input: String?): String {")
            appendLine("        return when (input) {")
            appendLine("            null -> \"null input\"")
            appendLine("            else -> \"processed: \$input\"")
            appendLine("        }")
            appendLine("    }")
            appendLine()
          }
          "getValue" -> {
            appendLine("    fun getValue(): String = value")
            appendLine()
          }
          "setValue" -> {
            appendLine("    fun setValue(newValue: String) {")
            appendLine("        value = newValue")
            appendLine("        count++")
            appendLine("    }")
            appendLine()
          }
          else -> {
            appendLine("    fun $memberName() {")
            appendLine("        // 自动生成的方法: $memberName")
            appendLine("        println(\"Executing $memberName\")")
            appendLine("    }")
            appendLine()
          }
        }
      }

      appendLine("}")
    }
  }

  /** 生成模拟的LibCodeResult对象 */
  fun generateMockLibCodeResult(
    className: String = "com.example.TestClass",
    language: String = "java",
    isDecompiled: Boolean = false,
    sourceType: SourceType = SourceType.SOURCE_JAR,
    includeMembers: List<String> = listOf("testMethod"),
  ): LibCodeResult {
    val sourceCode =
      when (language.lowercase()) {
        "kotlin",
        "kt" ->
          generateMockKotlinSource(
            packageName = className.substringBeforeLast('.', ""),
            className = className.substringAfterLast('.'),
            includeMembers = includeMembers,
          )
        else ->
          generateMockJavaSource(
            packageName = className.substringBeforeLast('.', ""),
            className = className.substringAfterLast('.'),
            includeMembers = includeMembers,
          )
      }

    val libraryName =
      when {
        className.startsWith("java.") -> "java-stdlib"
        className.startsWith("kotlin.") -> "kotlin-stdlib"
        className.contains(".") -> className.split(".").take(2).joinToString(".")
        else -> "unknown-library"
      }

    val version =
      when (sourceType) {
        SourceType.SOURCE_JAR -> "1.${Random.nextInt(0, 10)}.${Random.nextInt(0, 20)}"
        SourceType.DECOMPILED -> null
        SourceType.NOT_FOUND -> null
      }

    return LibCodeResult(
      sourceCode = sourceCode,
      isDecompiled = isDecompiled,
      language = language,
      metadata = LibCodeMetadata(libraryName = libraryName, version = version, sourceType = sourceType, documentation = generateMockDocumentation(className)),
    )
  }

  /** 生成模拟的文档字符串 */
  private fun generateMockDocumentation(className: String): String? {
    return if (Random.nextBoolean()) {
      "Mock documentation for $className. This class provides basic functionality for testing purposes."
    } else {
      null
    }
  }

  /** 生成模拟的JAR文件内容（字节数组） */
  fun generateMockJarContent(
    classes: Map<String, String> =
      mapOf(
        "com/example/TestClass.java" to generateMockJavaSource(),
        "com/example/util/Helper.java" to generateMockJavaSource("com.example.util", "Helper"),
        "META-INF/MANIFEST.MF" to generateManifest(),
      )
  ): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()

    JarOutputStream(byteArrayOutputStream).use { jarOut ->
      classes.forEach { (path, content) ->
        val entry = JarEntry(path)
        jarOut.putNextEntry(entry)
        jarOut.write(content.toByteArray())
        jarOut.closeEntry()
      }
    }

    return byteArrayOutputStream.toByteArray()
  }

  /** 生成模拟的MANIFEST.MF内容 */
  private fun generateManifest(): String {
    return buildString {
      appendLine("Manifest-Version: 1.0")
      appendLine("Created-By: MockDataGenerator")
      appendLine("Implementation-Title: Mock Test Library")
      appendLine("Implementation-Version: 1.0.0")
      appendLine("Implementation-Vendor: Test Vendor")
      appendLine()
    }
  }

  /** 创建临时JAR文件用于测试 */
  fun createTempJarFile(
    classes: Map<String, String> = mapOf("com/example/TestClass.java" to generateMockJavaSource()),
    prefix: String = "test-lib",
    suffix: String = ".jar",
  ): File {
    val tempFile = File.createTempFile(prefix, suffix)
    tempFile.deleteOnExit()

    FileOutputStream(tempFile).use { fileOut ->
      val jarContent = generateMockJarContent(classes)
      fileOut.write(jarContent)
    }

    return tempFile
  }

  /** 生成测试用的类名列表 */
  fun generateTestClassNames(count: Int = 10): List<String> {
    val packages = listOf("com.example", "org.test", "io.mock", "net.sample")
    val classTypes = listOf("Service", "Manager", "Handler", "Processor", "Controller", "Repository")

    return (1..count).map { index ->
      val pkg = packages.random()
      val type = classTypes.random()
      "$pkg.Test$type$index"
    }
  }

  /** 生成大型源码文件（用于性能测试） */
  fun generateLargeSourceCode(className: String = "LargeTestClass", methodCount: Int = 100, linesPerMethod: Int = 20): String {
    return buildString {
      appendLine("package com.example.large;")
      appendLine()
      appendLine("import java.util.*;")
      appendLine("import java.io.*;")
      appendLine("import java.math.*;")
      appendLine()
      appendLine("/**")
      appendLine(" * 大型测试类，包含 $methodCount 个方法")
      appendLine(" * 用于性能测试")
      appendLine(" */")
      appendLine("public class $className {")
      appendLine()

      // 生成大量字段
      repeat(50) { index -> appendLine("    private String field$index = \"value$index\";") }
      appendLine()

      // 生成大量方法
      repeat(methodCount) { methodIndex ->
        appendLine("    /**")
        appendLine("     * 自动生成的方法 $methodIndex")
        appendLine("     */")
        appendLine("    public void method$methodIndex() {")

        repeat(linesPerMethod) { lineIndex ->
          when (lineIndex % 4) {
            0 -> appendLine("        System.out.println(\"Line $lineIndex in method $methodIndex\");")
            1 -> appendLine("        String temp$lineIndex = \"temporary value $lineIndex\";")
            2 -> appendLine("        int calculation$lineIndex = $lineIndex * $methodIndex + ${Random.nextInt(100)};")
            3 -> appendLine("        // Comment line $lineIndex")
          }
        }

        appendLine("    }")
        appendLine()
      }

      appendLine("}")
    }
  }
}
