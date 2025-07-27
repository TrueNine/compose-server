# Version Catalog 模块

这个模块负责管理和发布项目生态系统中的依赖版本，提供集中化的版本管理和依赖更新检查功能。

## 功能特性

- 🔍 **依赖更新检查**: 使用 gradle-versions-plugin 检查所有依赖的最新版本
- 📊 **多格式报告**: 生成 HTML、JSON、XML、TXT 四种格式的报告
- 🚫 **版本过滤**: 自动过滤不稳定版本（alpha、beta、dev、snapshot）
- 📦 **版本目录**: 统一管理项目中所有模块的依赖版本

## 可用任务

### 主要任务

```bash
# 检查依赖更新并生成报告
./gradlew :version-catalog:dependencyUpdates

# 检查依赖更新 (别名)
./gradlew :version-catalog:checkUpdates

# 生成依赖更新报告并显示文件路径
./gradlew :version-catalog:updateReport
```

### 报告文件位置

执行任务后，报告文件将生成在 `version-catalog/build/dependencyUpdates/` 目录下：

- `report.html` - HTML 格式报告（推荐查看）
- `report.json` - JSON 格式报告（程序化处理）
- `report.xml` - XML 格式报告
- `report.txt` - 纯文本格式报告

## 配置说明

### 版本过滤规则

插件会自动过滤以下关键词的版本：
- `alpha` - Alpha 版本
- `beta` - Beta 版本  
- `dev` - 开发版本
- `snapshot` - 快照版本

### 输出格式

支持同时生成多种格式的报告：
- **HTML**: 适合人工查看，包含完整的依赖信息和链接
- **JSON**: 适合程序化处理和 CI/CD 集成
- **XML**: 适合与其他工具集成
- **TXT**: 纯文本格式，适合命令行查看

## 使用示例

### 1. 检查项目依赖更新

```bash
./gradlew :version-catalog:updateReport
```

输出示例：
```
依赖更新报告已生成:
  - HTML: C:\project\compose-server\version-catalog\build\dependencyUpdates\report.html
  - JSON: C:\project\compose-server\version-catalog\build\dependencyUpdates\report.json
  - XML:  C:\project\compose-server\version-catalog\build\dependencyUpdates\report.xml
  - TXT:  C:\project\compose-server\version-catalog\build\dependencyUpdates\report.txt
```

### 2. 在 CI/CD 中使用

```bash
# 检查依赖更新
./gradlew :version-catalog:dependencyUpdates

# 解析 JSON 报告进行自动化处理
cat version-catalog/build/dependencyUpdates/report.json | jq '.outdated.dependencies'
```

## 相关链接

- [gradle-versions-plugin 官方文档](https://github.com/ben-manes/gradle-versions-plugin)
- [Gradle Version Catalogs 文档](https://docs.gradle.org/current/userguide/platforms.html)
