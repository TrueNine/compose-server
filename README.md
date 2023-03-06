# 环境要求

- IntelliJ IDEA latest
- gradle 7.6
- openJDK 17.0.2
- kotlin 1.7.21
  注：开发机请准备 16GB 内存或以上，磁盘空出 10G 以上（windows 请在 C盘 留下 10G
  空间）

## 环境准备

- 添加阿里云云效 相关的环境变量

```text
YUNXIAO_USER 用户账号
YUNXIAO_PWD 用户密码
```

- 使用本地 gradle 执行 wrapper 拉起 gradlew
- 使用 gradlew check 检查项目

# 模块划分

- **biz** 业务模块
- **buildSrc** gradle 构建模块
- **cacheable** 缓存模块
- **core** 核心工具包
- **data-common** 爬虫模块
- **depend** 场景依赖模块
- **oss** 文件系统，对象存储模块
- **rds** 关系型数据库抽象模块
- **security** 鉴权模块
- **multi-test** 联合测试模块
- **web-api-doc** WEBAPI 文档模块

# 编码规范摘要

以下规范为后端编码人员必须遵守的一些实践应用经验

- 出现了 json 的大小写情况，使用 jackson 的 @JsonProperty，或者 Gson 的
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

- dao、dto、vo 等接口模型应当注解声明用途；并解析，注解声明，不应当被序列的，忽略 json
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
- 所有代码都必须过单元测试，把能想到的错误都压下来
- 所有代码采取 “2 空格” 缩进、80 个字符每行、lf 换行符、utf-8 编码，除了
  每行字数限制以外，其他的请遵守规约，如遇到没有规范的代码格式货字符集纠正之

# 代码提交规范

## 提交前提

- 每个人一条分支，取名规则是：id_分支，例如：alis_dev bob_test t_na_t_dev
- 尽量以小单位进行提交，尽量频繁提交，防止代码丢失

## 提交格式

- feat 新增功能、新增代码
- test 新增测试、修正测试等测试相关，代码本身问题不算
- fix 修复代码、修正bug等
- bug 提交的代码存在问题未修复
- doc 注释，文档等添加、修正、删除
- del 删除代码，只是单纯删除
- init 对项目进行初始化，某些模块进行初始化，首次提交代码
- todo 未完成的选项
