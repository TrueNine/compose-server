# 环境要求

- IntelliJ IDEA 最新版本 或者其他 IDE
- gradle 8.1
- openJDK 17.0.2

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

# 编码最佳实践

### json 序列化

- 一个工程内，不得出现一种以上的 json 序列化工具

出现了 json 的大小写情况，使用 jackson 的 @JsonProperty，或者 Gson 的
@SerializedName 进行注解，以避免编码风格婚礼

```java

@Data
public class AmazonS3Rule {
  @JsonProperty("Version")
  @SerializedName("Version")
  String version;

  @JsonProperty("Statement")
  @SerializedName("Statement")
  List<AmazonS3Statement> statement = new ArrayList<>();
}
```

### 实体模型

- 模型应当使用注释以及注解声明用途，注解优先于注释，两者同时使用
- 不应被序列的字段，使用相应注解进行忽略
- kotlin 对注解方面支持不是很好，尤其在 lombok 的支持上面，并且很多框架不支持反射 kotlin 的 data class

```java

@Schema(title = "品牌入参")
public class BrandRequestParam {

  @Id
  @JsonIgnore
  @Schema(hidden = true)
  @Expose(deserialize = false)
  private String id;

  /**
   * 名称
   */
  @Schema(title = "名称")
  private String title;
}
```

- jpa 会对查询出来的相同对象做浅拷贝，请做好深拷贝处理
- 数据库字段在删除时，如果使用了自定义方式绕过了备份监听器，请一定要处理好备份，但不提倡自定义方式

## 测试

所有代码都必须过单元测试，把能想到的错误都压下来

## 代码风格

- 所有代码采取 “2 空格” 缩进、160 个字符每行、lf 换行符、utf-8 编码

## 日志

- 不要在生产环境出现 println
- 打印日志时，使用跟 kotlin 相关的 infix 扩展方法，会卡住 IO 线程导致卡死
- 参数输出在 info 级别
- 系统输出在 debug 级别
- 步进输出在 trance 级别

## 版本控制

- 每人两条分支 test 和 dev
- 分支命名 `名_分支`，举例：bob_dev
- 尽量以小单位进行提交
- 提交时，按照：`名称 功能说明`  格式进行提交，举例如下：

```text
doc 添加对 xxx 类的修改
fix 修复登录请求问题
```

### 提交功能

| 名称   | 功能说明         |
|------|--------------|
| feat | 新增功能、新增代码    |
| test | 新增测试、修正测试等   |
| fix  | 修复功能         |
| bug  | 当前仍然存在问题     |
| todo | 未完成、但不存在 bug |
| doc  | 注释、文档        |
| del  | 删除功能、代码      |
| init | 进行初始化、首次提交代码 |

## 命名

- 实体类 Entity
- 仓库 Repository
- 实体类模型 Model
- 请求参数 RequestParam
- 
