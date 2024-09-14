/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 * 
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
*/
package net.yan100.compose.core.util

import net.yan100.compose.core.IString
import java.io.*
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * 系统资源定位器
 *
 * @author TrueNine
 * @since 2022-10-29
 */
@Deprecated("使用别的稳定的资源路径获取器")
object ResourcesLocator {
  private const val JAR_PROTOCOL = "jar"
  private const val FILE_PROTOCOL = "file"
  private val CLASS_SELF = ResourcesLocator::class.java
  private val LOADER_SELF: ClassLoader = CLASS_SELF.classLoader
  private const val GENERATED_DIR_NAME_SPACE = ".generated"
  private const val TEMP_DIR_NAME_SPACE = ".temp"
  private val APPLICATION_PROTOCOL: String

  /**
   * 获取一个根路径文件，区别如下： 1. 处于 ide 或者 classes 环境时，和 classes 同级 2. 处于 jar 时，为 jar 同级目录
   *
   * @return root dir
   */
  val appDir: File?
  private val GENERATED_DIR_FILE: File
  val tempDir: File
  private val ALL_CLASS_NAME = ConcurrentSkipListSet<String>()
  private var rootPath: String? = null

  init {
    APPLICATION_PROTOCOL = initApplicationProtocol()
    appDir = initApplicationDirectory()
    tempDir = File(appDir, TEMP_DIR_NAME_SPACE)
    GENERATED_DIR_FILE = File(appDir, GENERATED_DIR_NAME_SPACE)
    // 创建临时文件夹
    val a = tempDir.mkdirs()
    val b = GENERATED_DIR_FILE.mkdirs()
    initDefinedClasses()
  }

  private fun initApplicationProtocol(): String {
    return Objects.requireNonNull(ResourcesLocator::class.java.getResource("/")).protocol
  }

  private fun initDefinedClasses() {
    if (FILE_PROTOCOL == APPLICATION_PROTOCOL) {
      rootPath =
        Objects.requireNonNull<URL>(CLASS_SELF.getResource("/"))
          .toString()
          .replace("file:/", IString.EMPTY)
          .replace("\\", "/")
      scanFilePackages(File(rootPath))
    } else if (JAR_PROTOCOL == APPLICATION_PROTOCOL) {
      rootPath =
        CLASS_SELF
          .protectionDomain
          .codeSource
          .location
          .path
          .replace("file:/", IString.EMPTY)
          .split("!".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
      scanJarPackages(File(rootPath))
    }
  }

  private fun scanJarPackages(jarfile: File) {
    try {
      JarFile(jarfile).use { file ->
        val entries: Enumeration<JarEntry> = file.entries()
        while (entries.hasMoreElements()) {
          val entry: JarEntry = entries.nextElement()
          val name = entry.name
          if (name.endsWith(".class")) {
            val definedClassName =
              name.replace("\\", "/")
                .replace(rootPath!!, IString.EMPTY)
                .replace(".class", IString.EMPTY)
                .replace("/", ".")
            ALL_CLASS_NAME.add(definedClassName)
          }
        }
      }
    } catch (e: IOException) {
      throw RuntimeException(e)
    }
  }

  private fun scanFilePackages(classNode: File) {
    val fs = Objects.requireNonNull(classNode.listFiles())
    for (child in fs) {
      if (child.isDirectory) {
        scanFilePackages(child)
      } else {
        if (child.name.endsWith(".class")) {
          val defineClassName: String =
            child.absolutePath
              .replace("\\", "/")
              .replace(rootPath!!, IString.EMPTY)
              .replace(".class", IString.EMPTY)
              .replace("/", ".")
              .replace("BOOT-INF.classes.", IString.EMPTY)
          ALL_CLASS_NAME.add(defineClassName)
        }
      }
    }
  }

  val classNamePool: Set<String>
    get() = ALL_CLASS_NAME

  private fun initApplicationDirectory(): File? {
    // 初始化根路径
    val uri: URI
    try {
      uri = Objects.requireNonNull(ResourcesLocator::class.java.getResource("/")).toURI()
    } catch (e: URISyntaxException) {
      throw RuntimeException("获取文件根路径异常", e)
    }

    // 判断 是否为 jar 环境
    if (JAR_PROTOCOL == APPLICATION_PROTOCOL) {
      val meta = uri.toString()
      val jar = meta.replace("jar:file:/", "").split("!/".toRegex()).dropLastWhile { it.isEmpty() }
        .toTypedArray()[0]
      return File(jar).parentFile
    } else if (FILE_PROTOCOL == APPLICATION_PROTOCOL) {
      val path = Path.of(uri)
      return path.toFile()
    } else {
      return null
    }
  }

  fun classpathUrl(internalPath: String?): URL? {
    return LOADER_SELF.getResource(internalPath)
  }

  fun classpathInputStream(internalPath: String?): InputStream? {
    return try {
      LOADER_SELF.getResourceAsStream(internalPath)
    } catch (e: Throwable) {
      null
    }
  }

  fun classpathReader(internalPath: String?): BufferedReader {
    return BufferedReader(
      InputStreamReader(Objects.requireNonNull<InputStream?>(classpathInputStream(internalPath)))
    )
  }

  fun readClasspathByte(internalPath: String?): ByteArray? {
    try {
      classpathInputStream(internalPath).use { ins ->
        checkNotNull(ins)
        return ins.readAllBytes()
      }
    } catch (e: IOException) {
      return null
    } catch (e: NullPointerException) {
      return null
    }
  }

  val executeDir: File
    /**
     * 返回执行目录，区别如下： 1. 在 IDE 内 为项目根目录 2. 在 命令行执行情况下，为当前命令行的执行路径 3. 如果在被打包成 jar 后，为当前命令行的执行路径 其原理使用
     * 系统属性 user.dir 实现
     *
     *
     * 万不可将其作为获取 resources 的方式
     *
     * @return 执行目录
     */
    get() = File(System.getProperty("user.dir"))

  val generateDir: File
    get() {
      val gen =
        File(executeDir, "/" + GENERATED_DIR_NAME_SPACE)
      val created = gen.mkdirs()
      return gen
    }

  val generateDirPath: String
    get() = generateDir.absolutePath.replace("\\", "/")

  val executeTempDirectoryPath: String
    get() = tempDir.absolutePath.replace("\\", "/")

  fun createTempFile(filename: String): File {
    val f = File(tempDir, filename)
    try {
      val c = f.parentFile.mkdirs()
      val b = f.createNewFile()
    } catch (e: IOException) {
      throw RuntimeException("创建文件失败", e)
    }
    return f
  }

  fun createTempDir(dirname: String): File {
    val f = File(tempDir, dirname)
    val b = f.mkdirs()
    return f
  }

  fun createGenerateFile(path: String, name: String): File? {
    try {
      val gen = generateDir
      val parent = File(gen, path)
      val b = parent.mkdirs()
      val newFile = File(parent, name)
      val c = newFile.createNewFile()
      return newFile
    } catch (e: IOException) {
      return null
    }
  }

  fun createTempFile(path: String, name: String): File {
    return createTempFile("$path/$name")
  }

  fun createTempFile(path: String, name: String, descriptor: String): File {
    return createTempFile(path, "$name.$descriptor")
  }
}
