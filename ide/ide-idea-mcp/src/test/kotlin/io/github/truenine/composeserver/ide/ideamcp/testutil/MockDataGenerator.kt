package io.github.truenine.composeserver.ide.ideamcp.testutil

import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeMetadata
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeResult
import io.github.truenine.composeserver.ide.ideamcp.tools.SourceType
import java.io.*
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import kotlin.random.Random

/**
 * Mock data generator.
 *
 * Provides utility methods to generate mock data for tests:
 * - mock JAR files
 * - mock source files
 * - mock LibCodeResult objects
 * - test class definitions
 */
object MockDataGenerator {

  /** Generate mock Java source code. */
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
      appendLine(" * Mock generated test class")
      appendLine(" * Used for LibCodeService feature testing")
      appendLine(" */")
      appendLine("public class $className {")
      appendLine()

      // Generate fields
      appendLine("    private String value;")
      appendLine("    private int count;")
      appendLine("    private List<String> items;")
      appendLine()

      // Generate constructors
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

      // Generate specified member methods
      includeMembers.forEach { memberName ->
        when (memberName) {
          "testMethod" -> {
            appendLine("    /**")
            appendLine("     * Test method")
            appendLine("     * @param input input parameter")
            appendLine("     * @return processed result")
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
            appendLine("        // Auto-generated method: $memberName")
            appendLine("        System.out.println(\"Executing $memberName\");")
            appendLine("    }")
            appendLine()
          }
        }
      }

      // Generate toString method
      appendLine("    @Override")
      appendLine("    public String toString() {")
      appendLine("        return \"$className{\" +")
      appendLine("                \"value='\" + value + '\\'' +")
      appendLine("                \", count=\" + count +")
      appendLine("                \", items=\" + items +")
      appendLine("                '}';")
      appendLine("    }")
      appendLine()

      // Generate equals and hashCode
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

  /** Generate mock Kotlin source code. */
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
      appendLine(" * Mock generated Kotlin test class")
      appendLine(" * Used for LibCodeService feature testing")
      appendLine(" */")
      appendLine("data class $className(")
      appendLine("    var value: String = \"default\",")
      appendLine("    var count: Int = 0,")
      appendLine("    val items: MutableList<String> = mutableListOf()")
      appendLine(") {")
      appendLine()

      // Generate specified member methods
      includeMembers.forEach { memberName ->
        when (memberName) {
          "testMethod" -> {
            appendLine("    /**")
            appendLine("     * Test method")
            appendLine("     * @param input input parameter")
            appendLine("     * @return processed result")
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
            appendLine("        // Auto-generated method: $memberName")
            appendLine("        println(\"Executing $memberName\")")
            appendLine("    }")
            appendLine()
          }
        }
      }

      appendLine("}")
    }
  }

  /** Generate a mock LibCodeResult object. */
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

  /** Generate a mock documentation string. */
  private fun generateMockDocumentation(className: String): String? {
    return if (Random.nextBoolean()) {
      "Mock documentation for $className. This class provides basic functionality for testing purposes."
    } else {
      null
    }
  }

  /** Generate mock JAR file content as a byte array. */
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

  /** Generate mock MANIFEST.MF content. */
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

  /** Create a temporary JAR file for testing. */
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

  /** Generate a list of test class names. */
  fun generateTestClassNames(count: Int = 10): List<String> {
    val packages = listOf("com.example", "org.test", "io.mock", "net.sample")
    val classTypes = listOf("Service", "Manager", "Handler", "Processor", "Controller", "Repository")

    return (1..count).map { index ->
      val pkg = packages.random()
      val type = classTypes.random()
      "$pkg.Test$type$index"
    }
  }

  /** Generate a large source file (for performance testing). */
  fun generateLargeSourceCode(className: String = "LargeTestClass", methodCount: Int = 100, linesPerMethod: Int = 20): String {
    return buildString {
      appendLine("package com.example.large;")
      appendLine()
      appendLine("import java.util.*;")
      appendLine("import java.io.*;")
      appendLine("import java.math.*;")
      appendLine()
      appendLine("/**")
      appendLine(" * Large test class containing $methodCount methods")
      appendLine(" * Used for performance testing")
      appendLine(" */")
      appendLine("public class $className {")
      appendLine()

      // Generate many fields
      repeat(50) { index -> appendLine("    private String field$index = \"value$index\";") }
      appendLine()

      // Generate many methods
      repeat(methodCount) { methodIndex ->
        appendLine("    /**")
        appendLine("     * Auto-generated method $methodIndex")
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
