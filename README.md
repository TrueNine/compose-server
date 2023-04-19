# 环境要求

- IntelliJ IDEA latest
- gradle 8.1.0
- openJDK 17.0.2
- kotlin 1.8.20
  注：开发机请准备 16GB 内存或以上，磁盘空出 10G 以上（windows 请在 C盘 留下 10G
  空间）

## 环境准备

- 安装 gradle
- 确保系统的 JAVA_HOME 至少为 JDK17
- 添加阿里云云效 相关的环境变量
- 配置云效仓库地址

```text
YUNXIAO_USER 用户账号
YUNXIAO_PWD 用户密码
```

- 使用 gradle init 执行项目

```shell
gradle init
```

- 使用本地 gradle 执行 wrapper 拉起 gradlew

```shell
gradle wrapper
```

- 使用 gradlew 进行初始化和检查

```shell
./gradlew init
```

> 注：如果执行如上步骤错误，请反复检查。

# 模块划分

- **buildSrc** gradle 构建模块
- **cacheable** 缓存模块
- **core** 核心工具包
- **data-common** 数据工具模块
- **depend** 场景模块
- **oss** 对象存储模块
- **rds** 数据库模块
- **security** 鉴权模块
- **multi-test** 联合测试模块
- **web-api-doc** WEBAPI 文档模块

# 编码规范摘要

以下规范为所有人必须遵守的一些实践应用经验

### json 序列化

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

dao、dto、vo 等接口模型应当注解声明用途；并解析，注解声明，不应当被序列的，忽略 json
解析并使用
swagger 注解进行隐藏，以避免客户端开发人员的误解。这些模型出现问题则最好使用
java 建模，kotlin
对注解方面支持不是很好，尤其在 lombok 的支持上面，并且很多框架不支持反射 kotlin
的 data class

```java

@Schema(title = "品牌 vo")
public class BrandVo {

  @Id
  @JsonIgnore
  @Schema(hidden = true)
  @Expose(deserialize = false)
  private String id;

  /**
   * 名称（简短）
   */
  @Schema(name = "title", description = "名称（简短）")
  private String title;
}
```

- jpa 会对查询出来的相同对象做浅拷贝（抑或是kotlin），安全起见，请做好深拷贝处理
- 数据库字段在删除时，如果使用了自定义方式绕过了备份监听器，请一定要处理好备份，但不提倡自定义方式

## 测试

所有代码都必须过单元测试，把能想到的错误都压下来

## 代码风格

所有代码采取 “2 空格” 缩进、160 个字符每行、lf 换行符、utf-8 编码，除了
每行字数限制以外，其他的请遵守规约，如遇到没有规范的代码格式货字符集纠正。

## 日志

不要在打印日志时，使用跟 kotlin 相关的 infix 扩展方法，这样会卡住 IO 线程导致进程静止

输出日志时，与系统相关日志、参数日志、等等输出在 debug 级别

## 版本控制

1. 每人一条分支，取名规则是：id_分支，例如：alis_dev bob_test t_na_t_dev
2. 尽量以小单位进行提交

### 提交格式

提交时，按照：`名称 功能说明`  格式进行提交，举例如下：

```text
doc 添加对 xxx 类的修改
fix 修复登录请求问题
```



| 名称 | 功能说明                 |
| ---- | ------------------------ |
| feat | 新增功能、新增代码       |
| test | 新增测试、修正测试等     |
| fix  | 修复功能                 |
| bug  | 当前仍然存在问题         |
| todo | 未完成、但不存在 bug     |
| doc  | 注释、文档               |
| del  | 删除功能、代码           |
| init | 进行初始化、首次提交代码 |
