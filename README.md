# 环境要求

- IntelliJ IDEA 最新版本 或者其他 IDE
- gradle 8.1.1
- kotlin 1.8.20
- openJDK 17.0.7

> 注：开发机请准备 16GB 内存或以上，磁盘空出 10G 以上（windows 请在 C盘 留下 10G
> 空间）。
> 如果使用 IDEA，请分配 8G 内存

## 环境准备

- 确保系统的 JAVA_HOME 环境变量 至少为 JDK17

> 注：windows 在 path 内可以调整变量的优先级

- 添加阿里云云效 相关的环境变量

```text
YUNXIAO_USER 用户账号
YUNXIAO_PWD 用户密码
```

对上述代码做出优化，要求如下：
1. 输出内容：只输出代码
2. 输出要求：输出的代码尽量简洁
3. 受众：kotlin 后端开发程序员
4. 输出语言：kotlin
5. 可用测试框架：testNG、mockk、kotlin.test
6. 测试环境：springBoot

- 安装 gradle 并将其配置到环境变量

> 注：windows 将其配置到 path 内

- 进入项目当前目录 使用 wrapper 拉起 gradlew

```shell
gradle wrapper
```

- init 对项目进行初始化

```shell
./gradlew init
```

- 使用 gradlew 添加对各个 IDE 的支持

```shell
# idea 的支持
./gradlew idea

# eclipse 的支持
./gradlew eclipse

# visual studio 的支持
./gradlew visualStudio
```

- 使用 gradlew 进行初始化和检查

```shell
./gradlew check
```

- 可选：如果上一步生成 IDE 配置出错，可以清理这些配置文件

```shell
# 清理 IDEA
./gradlew cleanIdea

# 清理 eclipse
./gradlew cleanEclipse

# 清理 visual Studio
./gradlew cleanVisualStudio
```

如果执行如上步骤错误，请反复检查。

# 模块划分

- **buildSrc** gradle 构建模块
- **cacheable** 缓存模块
- **pay** 支付模块
- **core** 工具包
- **data-common** 数据工具模块
- **depend** 场景模块
- **oss** 对象存储模块
- **rds** 数据库模块
- **security** 鉴权模块
- **multi-test** 联合测试模块
- **web-api-doc** WEBAPI 文档模块

