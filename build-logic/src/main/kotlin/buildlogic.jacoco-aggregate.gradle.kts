plugins {
  id("jacoco")
}

// 使用默认的 JaCoCo 工具版本（继承自现有配置）

/**
 * 验证 CSV 报告的完整性和准确性
 * 检查 CSV 格式、数据结构和内容的有效性
 */
fun validateCsvReportIntegrity(csvFile: File) {
  logger.info("开始验证 CSV 报告完整性: ${csvFile.absolutePath}")

  if (!csvFile.exists() || !csvFile.canRead()) {
    throw IllegalStateException("CSV 报告文件不存在或不可读: ${csvFile.absolutePath}")
  }

  if (csvFile.length() == 0L) {
    throw IllegalStateException("CSV 报告文件为空: ${csvFile.absolutePath}")
  }

  try {
    val lines = csvFile.readLines()

    if (lines.isEmpty()) {
      throw IllegalStateException("CSV 报告没有内容")
    }

    // 验证 CSV 头部
    val header = lines.first()
    val expectedHeaders = listOf(
      "GROUP", "PACKAGE", "CLASS", "INSTRUCTION_MISSED", "INSTRUCTION_COVERED",
      "BRANCH_MISSED", "BRANCH_COVERED", "LINE_MISSED", "LINE_COVERED",
      "COMPLEXITY_MISSED", "COMPLEXITY_COVERED", "METHOD_MISSED", "METHOD_COVERED"
    )

    val actualHeaders = header.split(",").map { it.trim() }

    // 检查是否包含所有必需的列
    val missingHeaders = expectedHeaders.filter { it !in actualHeaders }
    if (missingHeaders.isNotEmpty()) {
      logger.warn("CSV 报告缺少以下列: ${missingHeaders.joinToString(", ")}")
    }

    // 检查是否有额外的列（这是正常的，JaCoCo 可能会添加新列）
    val extraHeaders = actualHeaders.filter { it !in expectedHeaders }
    if (extraHeaders.isNotEmpty()) {
      logger.info("CSV 报告包含额外的列: ${extraHeaders.joinToString(", ")}")
    }

    logger.info("CSV 报告头部验证通过，包含 ${actualHeaders.size} 列")

    // 验证数据行
    val dataLines = lines.drop(1) // 跳过头部
    if (dataLines.isEmpty()) {
      logger.warn("CSV 报告只有头部，没有数据行")
      return
    }

    var validDataRows = 0
    var invalidDataRows = 0

    dataLines.forEachIndexed { index, line ->
      val lineNumber = index + 2 // +2 因为跳过了头部且索引从0开始

      if (line.trim().isEmpty()) {
        logger.debug("跳过空行: 第 $lineNumber 行")
        return@forEachIndexed
      }

      val columns = line.split(",")

      if (columns.size != actualHeaders.size) {
        logger.warn("第 $lineNumber 行列数不匹配: 期望 ${actualHeaders.size}，实际 ${columns.size}")
        invalidDataRows++
        return@forEachIndexed
      }

      // 验证数值列是否为有效数字
      val numericColumnIndices = actualHeaders.mapIndexedNotNull { idx, header ->
        if (header.contains("MISSED") || header.contains("COVERED")) idx else null
      }

      var hasInvalidNumbers = false
      numericColumnIndices.forEach { colIndex ->
        val value = columns[colIndex].trim()
        if (value.isNotEmpty() && !value.matches(Regex("\\d+"))) {
          logger.debug("第 $lineNumber 行第 ${colIndex + 1} 列包含无效数字: '$value'")
          hasInvalidNumbers = true
        }
      }

      if (hasInvalidNumbers) {
        invalidDataRows++
      } else {
        validDataRows++
      }
    }

    logger.info("CSV 报告数据验证完成:")
    logger.info("  - 总数据行数: ${dataLines.size}")
    logger.info("  - 有效数据行数: $validDataRows")
    logger.info("  - 无效数据行数: $invalidDataRows")
    logger.info("  - 文件大小: ${csvFile.length()} bytes")

    if (validDataRows == 0) {
      throw IllegalStateException("CSV 报告没有有效的数据行")
    }

    if (invalidDataRows > 0) {
      logger.warn("CSV 报告包含 $invalidDataRows 个无效数据行，但仍有 $validDataRows 个有效数据行")
    }

    // 验证覆盖率数据的合理性
    validateCoverageDataConsistency(lines, actualHeaders)

    logger.info("✓ CSV 报告完整性验证通过")

  } catch (e: Exception) {
    logger.error("CSV 报告验证过程中发生错误: ${e.message}")
    throw e
  }
}

/**
 * 显示报告访问信息
 * 提供便捷的访问方式和命令行提示
 */
fun displayReportAccessInfo(reportPaths: List<String>) {
  logger.quiet("")
  logger.quiet("=".repeat(80))
  logger.quiet("JaCoCo 聚合报告生成完成")
  logger.quiet("=".repeat(80))

  if (reportPaths.isNotEmpty()) {
    logger.quiet("")
    logger.quiet("📊 生成的报告文件:")
    reportPaths.forEach { path ->
      logger.quiet("   $path")
    }

    // 提供便捷的访问命令
    logger.quiet("")
    logger.quiet("🚀 便捷访问命令:")
    logger.quiet("   查看 HTML 报告: open build/reports/jacoco/aggregate/html/index.html")
    logger.quiet("   查看 XML 报告:  cat build/reports/jacoco/aggregate/jacoco.xml")
    logger.quiet("   查看 CSV 报告:  cat build/reports/jacoco/aggregate/jacoco.csv")

    logger.quiet("")
    logger.quiet("📁 报告目录: build/reports/jacoco/aggregate/")
    logger.quiet("   ├── html/           # 人类可读的 HTML 报告")
    logger.quiet("   ├── jacoco.xml      # AI 友好的 XML 报告")
    logger.quiet("   └── jacoco.csv      # 数据分析友好的 CSV 报告")

    logger.quiet("")
    logger.quiet("💡 提示:")
    logger.quiet("   - HTML 报告提供交互式覆盖率浏览体验")
    logger.quiet("   - XML 报告适合 AI 系统和自动化分析")
    logger.quiet("   - CSV 报告适合数据分析和统计处理")

    logger.quiet("")
    logger.quiet("🔄 重新生成报告: ./gradlew jacocoAggregateReport")
    logger.quiet("🔄 简化命令别名: ./gradlew jacoco")
  } else {
    logger.quiet("")
    logger.quiet("❌ 未生成任何报告文件，请检查构建日志")
  }

  logger.quiet("=".repeat(80))
  logger.quiet("")
}

/**
 * 验证覆盖率数据的一致性
 * 检查 MISSED + COVERED 的数据逻辑是否合理
 */
fun validateCoverageDataConsistency(lines: List<String>, headers: List<String>) {
  logger.info("开始验证覆盖率数据一致性...")

  val dataLines = lines.drop(1) // 跳过头部
  if (dataLines.isEmpty()) return

  // 找到相关列的索引
  val instructionMissedIdx = headers.indexOf("INSTRUCTION_MISSED")
  val instructionCoveredIdx = headers.indexOf("INSTRUCTION_COVERED")
  val branchMissedIdx = headers.indexOf("BRANCH_MISSED")
  val branchCoveredIdx = headers.indexOf("BRANCH_COVERED")
  val lineMissedIdx = headers.indexOf("LINE_MISSED")
  val lineCoveredIdx = headers.indexOf("LINE_COVERED")

  var inconsistentRows = 0
  var totalDataRows = 0

  dataLines.forEach { line ->
    if (line.trim().isEmpty()) return@forEach

    totalDataRows++
    val columns = line.split(",")

    if (columns.size != headers.size) return@forEach

    try {
      // 验证指令覆盖率数据
      if (instructionMissedIdx >= 0 && instructionCoveredIdx >= 0) {
        val missed = columns[instructionMissedIdx].trim().toIntOrNull() ?: 0
        val covered = columns[instructionCoveredIdx].trim().toIntOrNull() ?: 0

        if (missed < 0 || covered < 0) {
          logger.debug("发现负数覆盖率数据: missed=$missed, covered=$covered")
          inconsistentRows++
        }
      }

      // 验证分支覆盖率数据
      if (branchMissedIdx >= 0 && branchCoveredIdx >= 0) {
        val missed = columns[branchMissedIdx].trim().toIntOrNull() ?: 0
        val covered = columns[branchCoveredIdx].trim().toIntOrNull() ?: 0

        if (missed < 0 || covered < 0) {
          logger.debug("发现负数分支覆盖率数据: missed=$missed, covered=$covered")
          inconsistentRows++
        }
      }

      // 验证行覆盖率数据
      if (lineMissedIdx >= 0 && lineCoveredIdx >= 0) {
        val missed = columns[lineMissedIdx].trim().toIntOrNull() ?: 0
        val covered = columns[lineCoveredIdx].trim().toIntOrNull() ?: 0

        if (missed < 0 || covered < 0) {
          logger.debug("发现负数行覆盖率数据: missed=$missed, covered=$covered")
          inconsistentRows++
        }
      }

    } catch (e: NumberFormatException) {
      logger.debug("数据格式错误: ${e.message}")
      inconsistentRows++
    }
  }

  logger.info("覆盖率数据一致性验证完成:")
  logger.info("  - 检查的数据行数: $totalDataRows")
  logger.info("  - 不一致的数据行数: $inconsistentRows")

  if (inconsistentRows > 0) {
    val inconsistencyRate = (inconsistentRows.toDouble() / totalDataRows * 100).toInt()
    if (inconsistencyRate > 10) { // 如果超过10%的数据不一致，则警告
      logger.warn("CSV 报告中有 $inconsistencyRate% 的数据行存在不一致，请检查数据质量")
    } else {
      logger.info("发现少量数据不一致（$inconsistencyRate%），在可接受范围内")
    }
  } else {
    logger.info("✓ 所有覆盖率数据一致性检查通过")
  }
}

/**
 * 配置聚合任务的依赖关系和执行顺序
 * 确保任务依赖的正确性和并行执行的安全性
 */
fun Task.configureTaskDependencies() {
  logger.info("配置 JaCoCo 聚合任务的依赖关系...")

  // 使用 Provider API 延迟配置，在任务执行时动态发现有效子项目
  val validSubprojects = provider { discoverValidSubprojects() }

  // 设置对所有有效子项目测试任务的显式依赖
  // 使用 Provider API 确保依赖关系在配置阶段正确建立
  dependsOn(validSubprojects.map { projects ->
    projects.map { project ->
      val testTaskPath = "${project.path}:test"
      logger.debug("添加测试任务依赖: $testTaskPath")
      testTaskPath
    }
  })

  // 配置与 Spotless 任务的执行顺序
  // 确保 Spotless 任务在聚合任务之前完成，避免文件格式化冲突
  mustRunAfter(tasks.withType<com.diffplug.gradle.spotless.SpotlessTask>())
  logger.debug("配置 Spotless 任务执行顺序依赖")

  // 配置与其他验证任务的执行顺序
  // 确保编译任务在聚合任务之前完成
  mustRunAfter(tasks.withType(org.gradle.api.tasks.compile.JavaCompile::class))
  mustRunAfter(tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class))
  logger.debug("配置编译任务执行顺序依赖")

  // 确保并行执行的安全性
  // 通过显式依赖避免隐式依赖问题
  outputs.upToDateWhen { false } // 确保任务总是执行，因为聚合数据可能发生变化

  // 配置任务输入输出，支持 Gradle 构建缓存和增量构建
  inputs.files(validSubprojects.map { projects ->
    projects.flatMap { project ->
      try {
        // 监控执行数据文件的变化
        val execFiles = project.fileTree(project.layout.buildDirectory) {
          include("**/jacoco/test.exec", "**/jacoco.exec")
        }
        execFiles.files
      } catch (e: Exception) {
        logger.debug("配置阶段跳过 ${project.path} 的输入文件监控: ${e.message}")
        emptyList<File>()
      }
    }
  })

  // 配置输出目录
  outputs.dir(layout.buildDirectory.dir("reports/jacoco/aggregate"))

  logger.info("JaCoCo 聚合任务依赖关系配置完成")
}

/**
 * 自动发现有效的子项目
 * 筛选条件：
 * 1. 应用了 jacoco 插件
 * 2. 有 test 任务
 * 3. 有源代码目录（src/main/kotlin 或 src/main/java）
 * 注意：执行数据文件的存在性检查在任务执行时进行，而不是在配置阶段
 */
fun discoverValidSubprojects(): List<Project> {
  logger.info("开始自动发现有效的子项目...")

  val validProjects = subprojects.filter { subproject ->
    try {
      // 检查是否应用了 jacoco 插件
      val hasJacocoPlugin = subproject.plugins.hasPlugin("jacoco")
      if (!hasJacocoPlugin) {
        logger.debug("跳过子项目 ${subproject.path}: 未应用 jacoco 插件")
        return@filter false
      }

      // 检查是否有 test 任务
      val hasTestTask = subproject.tasks.findByName("test") != null
      if (!hasTestTask) {
        logger.debug("跳过子项目 ${subproject.path}: 未找到 test 任务")
        return@filter false
      }

      // 检查是否有源代码目录
      val kotlinSrcDir = subproject.file("src/main/kotlin")
      val javaSrcDir = subproject.file("src/main/java")
      val hasSourceCode = kotlinSrcDir.exists() || javaSrcDir.exists()

      if (!hasSourceCode) {
        logger.debug("跳过子项目 ${subproject.path}: 未找到源代码目录")
        return@filter false
      }

      // 检查是否有编译输出目录（可选检查，用于提前发现潜在问题）
      val kotlinClassDir = subproject.layout.buildDirectory.dir("classes/kotlin/main").get().asFile
      val javaClassDir = subproject.layout.buildDirectory.dir("classes/java/main").get().asFile
      val hasCompiledClasses = kotlinClassDir.exists() || javaClassDir.exists()

      if (!hasCompiledClasses) {
        logger.debug("子项目 ${subproject.path} 尚未编译，将在任务执行时重新检查")
      }

      logger.info("包含子项目 ${subproject.path} 到 JaCoCo 聚合报告中")
      return@filter true

    } catch (e: Exception) {
      // 安全处理：任何异常都跳过该子项目，避免构建失败
      logger.warn("跳过子项目 ${subproject.path}，发生错误: ${e.message}")
      return@filter false
    }
  }

  logger.info("发现 ${validProjects.size} 个有效子项目用于 JaCoCo 聚合")
  validProjects.forEach { project ->
    logger.info("  - ${project.path}")
  }

  return validProjects
}

/**
 * 收集有效的执行数据文件
 * 与现有约定插件保持完全一致的文件匹配模式和验证逻辑
 */
fun collectValidExecutionData(projects: List<Project>): List<File> {
  logger.info("开始收集执行数据文件...")

  val validExecFiles = projects.flatMap { project ->
    try {
      // 使用与现有约定插件完全相同的文件匹配模式
      val execFileTree = project.fileTree(project.layout.buildDirectory) {
        include("**/jacoco/test.exec", "**/jacoco.exec")
      }

      val projectExecFiles = execFileTree.files.filter { execFile ->
        when {
          !execFile.exists() -> {
            logger.debug("跳过 ${project.path}: 执行数据文件不存在 ${execFile.absolutePath}")
            false
          }
          !execFile.canRead() -> {
            logger.debug("跳过 ${project.path}: 执行数据文件不可读 ${execFile.absolutePath}")
            false
          }
          execFile.length() == 0L -> {
            logger.debug("跳过 ${project.path}: 执行数据文件为空 ${execFile.absolutePath}")
            false
          }
          else -> {
            logger.info("找到有效的执行数据文件: ${execFile.absolutePath} (${execFile.length()} bytes)")
            true
          }
        }
      }

      if (projectExecFiles.isEmpty()) {
        logger.debug("子项目 ${project.path} 没有有效的执行数据文件")
      } else {
        logger.info("子项目 ${project.path} 包含 ${projectExecFiles.size} 个执行数据文件")
      }

      projectExecFiles

    } catch (e: Exception) {
      logger.warn("访问 ${project.path} 的执行数据时发生错误: ${e.message}")
      emptyList<File>()
    }
  }

  logger.info("收集到 ${validExecFiles.size} 个有效的执行数据文件")
  return validExecFiles
}

/**
 * 收集有效的类文件目录
 * 应用与现有约定插件完全相同的排除规则
 */
fun collectValidClassDirectories(projects: List<Project>): List<ConfigurableFileTree> {
  logger.info("开始收集类文件目录...")

  val validClassDirs = projects.flatMap { project ->
    try {
      val classDirs = mutableListOf<ConfigurableFileTree>()

      // 检查 Kotlin 编译输出
      val kotlinClassDir = project.layout.buildDirectory.dir("classes/kotlin/main").get().asFile
      if (kotlinClassDir.exists() && kotlinClassDir.listFiles()?.isNotEmpty() == true) {
        val kotlinFileTree = project.fileTree(kotlinClassDir) {
          // 应用与现有约定插件完全相同的排除规则
          exclude("**/generated/**")
        }

        if (kotlinFileTree.files.isNotEmpty()) {
          classDirs.add(kotlinFileTree)
          logger.info("找到 Kotlin 类文件目录: ${kotlinClassDir.absolutePath} (${kotlinFileTree.files.size} 个类文件)")
        } else {
          logger.debug("Kotlin 类文件目录为空或全部被排除: ${kotlinClassDir.absolutePath}")
        }
      } else {
        logger.debug("Kotlin 类文件目录不存在或为空: ${kotlinClassDir.absolutePath}")
      }

      // 检查 Java 编译输出
      val javaClassDir = project.layout.buildDirectory.dir("classes/java/main").get().asFile
      if (javaClassDir.exists() && javaClassDir.listFiles()?.isNotEmpty() == true) {
        val javaFileTree = project.fileTree(javaClassDir) {
          // 应用与现有约定插件完全相同的排除规则
          exclude("**/generated/**")
        }

        if (javaFileTree.files.isNotEmpty()) {
          classDirs.add(javaFileTree)
          logger.info("找到 Java 类文件目录: ${javaClassDir.absolutePath} (${javaFileTree.files.size} 个类文件)")
        } else {
          logger.debug("Java 类文件目录为空或全部被排除: ${javaClassDir.absolutePath}")
        }
      } else {
        logger.debug("Java 类文件目录不存在或为空: ${javaClassDir.absolutePath}")
      }

      if (classDirs.isEmpty()) {
        logger.debug("跳过 ${project.path}: 未找到有效的类文件目录")
      } else {
        logger.info("子项目 ${project.path} 包含 ${classDirs.size} 个类文件目录")
      }

      classDirs

    } catch (e: Exception) {
      logger.warn("访问 ${project.path} 的类文件目录时发生错误: ${e.message}")
      emptyList<ConfigurableFileTree>()
    }
  }

  logger.info("收集到 ${validClassDirs.size} 个有效的类文件目录")
  return validClassDirs
}

/**
 * 收集有效的源代码目录
 * 与现有约定插件完全保持一致的源代码目录配置
 */
fun collectValidSourceDirectories(projects: List<Project>): List<File> {
  logger.info("开始收集源代码目录...")

  val validSourceDirs = projects.flatMap { project ->
    try {
      val sourceDirs = mutableListOf<File>()

      // 与现有约定插件完全一致的源代码目录列表
      val mainSrcDirs = listOf("src/main/java", "src/main/kotlin")

      mainSrcDirs.forEach { srcPath ->
        val srcDir = project.file(srcPath)
        if (srcDir.exists() && srcDir.listFiles()?.isNotEmpty() == true) {
          sourceDirs.add(srcDir)
          logger.info("找到源代码目录: ${srcDir.absolutePath}")
        } else {
          logger.debug("跳过 ${project.path}: 源代码目录不存在或为空 ${srcPath}")
        }
      }

      if (sourceDirs.isEmpty()) {
        logger.debug("跳过 ${project.path}: 未找到有效的源代码目录")
      } else {
        logger.info("子项目 ${project.path} 包含 ${sourceDirs.size} 个源代码目录")
      }

      sourceDirs

    } catch (e: Exception) {
      logger.warn("访问 ${project.path} 的源代码目录时发生错误: ${e.message}")
      emptyList<File>()
    }
  }

  logger.info("收集到 ${validSourceDirs.size} 个有效的源代码目录")
  return validSourceDirs
}

// 注册聚合报告任务
tasks.register<JacocoReport>("jacocoAggregateReport") {
  group = "verification"
  description = "Generates aggregate JaCoCo coverage report for all subprojects"

  // 设置报告输出目录
  val aggregateReportDir = layout.buildDirectory.dir("reports/jacoco/aggregate")

  // 配置多格式报告输出
  reports {
    // HTML 格式报告（人类可读）
    html.required.set(true)
    html.outputLocation.set(aggregateReportDir.map { it.dir("html") })

    // XML 格式报告（AI 友好，结构化）
    xml.required.set(true)
    xml.outputLocation.set(aggregateReportDir.map { it.file("jacoco.xml") })

    // CSV 格式报告（数据分析友好，AI 友好的额外格式）
    csv.required.set(true)
    csv.outputLocation.set(aggregateReportDir.map { it.file("jacoco.csv") })
  }

  // 使用 Provider API 延迟配置，在任务执行时动态发现有效子项目
  val validSubprojects = provider { discoverValidSubprojects() }

  // 配置任务依赖关系和执行顺序
  configureTaskDependencies()

  // 使用 Provider API 延迟配置执行数据，与现有约定插件保持完全一致的文件匹配模式
  executionData.setFrom(
    validSubprojects.map { projects ->
      projects.flatMap { project ->
        try {
          // 使用与现有约定插件完全相同的执行数据收集逻辑
          project.fileTree(project.layout.buildDirectory) {
            include("**/jacoco/test.exec", "**/jacoco.exec")
          }.files.filter { execFile ->
            execFile.exists() && execFile.length() > 0 && execFile.canRead()
          }
        } catch (e: Exception) {
          logger.debug("配置阶段跳过 ${project.path} 的执行数据: ${e.message}")
          emptyList<File>()
        }
      }
    }
  )

  // 使用与现有约定插件完全相同的类文件目录配置和排除规则
  classDirectories.setFrom(
    validSubprojects.map { projects ->
      projects.flatMap { project ->
        try {
          val classDirs = mutableListOf<ConfigurableFileTree>()

          // Kotlin 编译输出
          val kotlinClassDir = project.layout.buildDirectory.dir("classes/kotlin/main").get().asFile
          if (kotlinClassDir.exists()) {
            classDirs.add(project.fileTree(kotlinClassDir) {
              // 应用与现有约定插件完全相同的排除规则
              exclude("**/generated/**")
            })
          }

          // Java 编译输出
          val javaClassDir = project.layout.buildDirectory.dir("classes/java/main").get().asFile
          if (javaClassDir.exists()) {
            classDirs.add(project.fileTree(javaClassDir) {
              // 应用与现有约定插件完全相同的排除规则
              exclude("**/generated/**")
            })
          }

          classDirs
        } catch (e: Exception) {
          logger.debug("配置阶段跳过 ${project.path} 的类文件: ${e.message}")
          emptyList<ConfigurableFileTree>()
        }
      }
    }
  )

  // 使用与现有约定插件完全一致的源代码目录配置
  sourceDirectories.setFrom(
    validSubprojects.map { projects ->
      projects.flatMap { project ->
        try {
          val sourceDirs = mutableListOf<File>()
          // 与现有约定插件完全一致的源代码目录列表
          val mainSrcDirs = listOf("src/main/java", "src/main/kotlin")

          mainSrcDirs.forEach { srcPath ->
            val srcDir = project.file(srcPath)
            if (srcDir.exists()) {
              sourceDirs.add(srcDir)
            }
          }

          sourceDirs
        } catch (e: Exception) {
          logger.debug("配置阶段跳过 ${project.path} 的源代码: ${e.message}")
          emptyList<File>()
        }
      }
    }
  )

  // 在任务执行时进行详细的验证和日志记录
  doFirst {
    val projects = validSubprojects.get()
    if (projects.isEmpty()) {
      logger.warn("未找到有效的子项目用于 JaCoCo 聚合")
      logger.warn("请确保子项目已应用 jacoco 插件并定义了 test 任务")
      throw GradleException("没有找到可用于聚合的子项目")
    }

    logger.info("从 ${projects.size} 个子项目中发现执行数据:")
    projects.forEach { project ->
      logger.info("  - ${project.path}")
    }

    // 验证执行数据文件
    val validExecFiles = collectValidExecutionData(projects)
    if (validExecFiles.isEmpty()) {
      logger.warn("未找到有效的执行数据文件")
      logger.warn("请先运行测试: ./gradlew test")
      logger.warn("或者运行特定模块的测试，例如: ./gradlew :shared:test")
      throw GradleException("没有找到可用的 JaCoCo 执行数据文件")
    }

    logger.info("找到 ${validExecFiles.size} 个有效的执行数据文件")

    // 验证类文件目录
    val classDirectories = collectValidClassDirectories(projects)
    if (classDirectories.isEmpty()) {
      logger.warn("未找到有效的类文件目录")
      logger.warn("请先编译项目: ./gradlew compileKotlin compileJava")
      throw GradleException("没有找到可用的编译类文件")
    }

    logger.info("配置了来自 ${classDirectories.size} 个项目的类文件目录")

    // 验证源代码目录
    val sourceDirectories = collectValidSourceDirectories(projects)
    if (sourceDirectories.isEmpty()) {
      logger.warn("未找到有效的源代码目录")
      throw GradleException("没有找到可用的源代码目录")
    }

    logger.info("配置了来自 ${sourceDirectories.size} 个项目的源代码目录")
  }

  doLast {
    val projects = validSubprojects.get()
    val validExecFiles = collectValidExecutionData(projects)

    logger.info("JaCoCo 聚合报告生成成功，包含 ${validExecFiles.size} 个执行数据文件")

    // 验证报告文件是否成功生成并输出详细路径信息
    val htmlReport = reports.html.outputLocation.get().asFile
    val xmlReport = reports.xml.outputLocation.get().asFile
    val csvReport = reports.csv.outputLocation.get().asFile

    val reportPaths = mutableListOf<String>()

    if (htmlReport.exists()) {
      logger.info("✓ HTML 报告已生成: ${htmlReport.absolutePath}")
      reportPaths.add("HTML: ${htmlReport.absolutePath}")
    } else {
      logger.warn("✗ HTML 报告生成失败")
    }

    if (xmlReport.exists()) {
      logger.info("✓ XML 报告已生成: ${xmlReport.absolutePath}")
      reportPaths.add("XML: ${xmlReport.absolutePath}")
    } else {
      logger.warn("✗ XML 报告生成失败")
    }

    if (csvReport.exists()) {
      logger.info("✓ CSV 报告已生成: ${csvReport.absolutePath}")
      reportPaths.add("CSV: ${csvReport.absolutePath}")

      // 验证 CSV 数据的完整性和准确性
      try {
        validateCsvReportIntegrity(csvReport)
      } catch (e: Exception) {
        logger.warn("CSV 报告验证失败: ${e.message}")
      }
    } else {
      logger.warn("✗ CSV 报告生成失败")
    }

    // 输出聚合统计信息
    logger.info("聚合统计:")
    logger.info("  - 包含的子项目数: ${projects.size}")
    logger.info("  - 有效的���行数据文件数: ${validExecFiles.size}")
    logger.info("  - 报告输出目录: ${layout.buildDirectory.dir("reports/jacoco/aggregate").get().asFile.absolutePath}")

    // 输出便捷访问信息
    displayReportAccessInfo(reportPaths)
  }
}

// 创建便捷的任务别名，提供单一入口访问
tasks.register("jacoco") {
  group = "verification"
  description = "Convenient alias for jacocoAggregateReport - generates all format coverage reports"
  dependsOn("jacocoAggregateReport")

  doLast {
    logger.quiet("")
    logger.quiet("✅ JaCoCo 聚合报告任务完成")
    logger.quiet("💡 使用 './gradlew jacoco' 可以快速重新生成报告")
  }
}

// 创建报告查看任务
tasks.register("jacocoView") {
  group = "verification"
  description = "Display JaCoCo aggregate report paths and access information"

  doLast {
    val aggregateReportDir = layout.buildDirectory.dir("reports/jacoco/aggregate").get().asFile
    val htmlReport = File(aggregateReportDir, "html/index.html")
    val xmlReport = File(aggregateReportDir, "jacoco.xml")
    val csvReport = File(aggregateReportDir, "jacoco.csv")

    val reportPaths = mutableListOf<String>()

    if (htmlReport.exists()) {
      reportPaths.add("HTML: ${htmlReport.absolutePath}")
    }
    if (xmlReport.exists()) {
      reportPaths.add("XML: ${xmlReport.absolutePath}")
    }
    if (csvReport.exists()) {
      reportPaths.add("CSV: ${csvReport.absolutePath}")
    }

    if (reportPaths.isNotEmpty()) {
      displayReportAccessInfo(reportPaths)
    } else {
      logger.quiet("")
      logger.quiet("❌ 未找到 JaCoCo 聚合报告文件")
      logger.quiet("💡 请先运行: ./gradlew jacoco")
      logger.quiet("")
    }
  }
}

// 创建报告清理任务
tasks.register<Delete>("jacocoClean") {
  group = "verification"
  description = "Clean JaCoCo aggregate reports"
  delete(layout.buildDirectory.dir("reports/jacoco/aggregate"))

  doLast {
    logger.quiet("🧹 JaCoCo 聚合报告已清理")
    logger.quiet("💡 运行 './gradlew jacoco' 重新生成报告")
  }
}

// 创建帮助任务，显示所有便捷命令
tasks.register("jacocoHelp") {
  group = "help"
  description = "Display help information for JaCoCo aggregate tasks"

  doLast {
    logger.quiet("")
    logger.quiet("=".repeat(80))
    logger.quiet("JaCoCo 聚合报告 - 便捷命令帮助")
    logger.quiet("=".repeat(80))
    logger.quiet("")
    logger.quiet("📋 可用命令:")
    logger.quiet("")
    logger.quiet("  ./gradlew jacoco")
    logger.quiet("    ├─ 生成所有格式的聚合覆盖率报告")
    logger.quiet("    ├─ 包含 HTML、XML、CSV 三种格式")
    logger.quiet("    └─ 等同于 './gradlew jacocoAggregateReport'")
    logger.quiet("")
    logger.quiet("  ./gradlew jacocoView")
    logger.quiet("    ├─ 显示现有报告文件的路径信息")
    logger.quiet("    ├─ 提供便捷访问命令")
    logger.quiet("    └─ 无需重新生成报告")
    logger.quiet("")
    logger.quiet("  ./gradlew jacocoClean")
    logger.quiet("    ├─ 清理所有聚合报告文件")
    logger.quiet("    └─ 释放磁盘空间")
    logger.quiet("")
    logger.quiet("  ./gradlew jacocoHelp")
    logger.quiet("    └─ 显示此帮助信息")
    logger.quiet("")
    logger.quiet("📊 报告格式说明:")
    logger.quiet("  • HTML 格式: 人类可读，提供交互式浏览体验")
    logger.quiet("  • XML 格式:  AI 友好，结构化数据，适合自动化分析")
    logger.quiet("  • CSV 格式:  数据分析友好，适合统计处理")
    logger.quiet("")
    logger.quiet("📁 报告输出位置: build/reports/jacoco/aggregate/")
    logger.quiet("")
    logger.quiet("💡 使用建议:")
    logger.quiet("  1. 首次使用: ./gradlew jacoco")
    logger.quiet("  2. 查看报告: ./gradlew jacocoView")
    logger.quiet("  3. 清理报告: ./gradlew jacocoClean")
    logger.quiet("")
    logger.quiet("=".repeat(80))
    logger.quiet("")
  }
}
