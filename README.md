# Compose Server

## 项目介绍

这是一个基于 以下技术构建的服务器端 sdk

- [Kotlin](https://kotlinlang.org/)
- [Gradle](https://gradle.org/)
- [SpringBoot](https://spring.io/projects/spring-boot)

其采用多项目模块化设计

## 环境要求

```envRequirement
java: 21.0.2
kotlin: 2.0.10-RC
gradle: 8.9
```

> 注：开发机请准备 16GB 内存或以上，磁盘空出 10G 以上（windows 请在 C盘 留下 10G
> 空间）。
> 如果使用 IDEA，请分配 8G 内存

## 环境准备

- 确保系统的 JAVA_HOME 环境变量 至少为 JDK21+

> 注：windows 在 path 内可以调整变量的优先级

- 安装 gradle 并将其配置到环境变量 `GRADLE_HOME`

> 注：windows 将其配置到 path 内

- init 对项目进行初始化

```shell
# 初始化项目
gradle init

# 生成 gradle wrapper
gradle wrapper

# 检查当前项目
./gradlew check
```
