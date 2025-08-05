# maven 中央仓库配置

1. 打开 [maven central account](https://central.sonatype.com/account)
2. 自己签发个 account 密钥，然后一定要记住或者保存到本地，但别发给别人或加入远程仓库

# gpg 签名

这一步重点在于：生成密钥对、拿取 `keyId`、发布到 keyserver。

> 由于 SKS 密钥服务器网络已被弃用，我们建议使用 特定的 GPG 密钥服务器。 中央服务器当前支持的 GPG 密钥服务器是：
>
> - `keyserver.ubuntu.com`
> - `keys.openpgp.org`
> - `pgp.mit.edu`

1. `gpg --version` 验证 gpg 是否安装，如果没有请自行搞定。
2. `gpg --full-gen-key` 按照提示生成密钥对
3. `gpg --list-keys --keyid-format=short` 可以列出已经生成的 keys，注意 `pub` 段的 `/` 后面的是你的 `keyId`，第二行那串长的下一步要用到。

```text
---------------------------------
pub   ed25aaaa/B8eeee 2025-07-05 [SC]
      64A4xxxxxxxx370273xxxxxxxxxx
uid           [ 绝对 ] TrueNine (TrueNine) <truenine304520@gmail.com>
sub   xxxx/dddd 2025-07-05 [E]
```

4. `gpg --keyserver <keyserver.domain.com> --send-keys <长串>` 发布到 keyserver,地址写对就行,那一长串是 `--list-keys` 里的 `pub`
   段搞出来的
5. `gpg --export-secret-keys <keyId> > /home/truenine/.gradle/private.gpg` 保存 private.gpg 到你自己的 `.gradle` 下，不要放到仓库里面

> 使用 `gpg --armor --export-secret-keys <keyId>` 可以输出私钥字符串格式，这对于 CI 环境可能有用。

# gradle 准备

按照 github 中：[vanniktech 插件文档](https://github.com/vanniktech/gradle-maven-publish-plugin) 对插件进行配置，填写好所有的 maven 配置信息。

准备如下内容（非必要，方便你复制插件地址的）`libs.versions.toml`

```toml
[versions]

com-vanniktech-maven-publish = "0.33.0" # 版本号自己去中央仓库找最新的，别瞎比赖赖照抄然后错误找我

[libraries]

com-vanniktech-maven-publish-com-vanniktech-maven-publish-gradle-plugin = { module = "com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin", version.ref = "com-vanniktech-maven-publish" }

[plugins]

com-vanniktech-maven-publish = { id = "com.vanniktech.maven.publish", version.ref = "com-vanniktech-maven-publish" }

```

剩下的你自己决定怎么应用插件
> 插件的任务在 `release` 下，而不是 `publish` 下

将属性配置到 `~/.gradle/gradle.properties`

```properties
# --list-keys 拿到的 keyId
signing.keyId=keyId
# 生成密钥时使用的密码
signing.password=password
# 这里要写绝对路径
signing.secretKeyRingFile=/home/truenine/.gradle/private.gpg
```

## CI 环境可配置命令

```shell
./gradlew publishToMavenCentral \
  --no-daemon \
  --stacktrace \
  --info \
  --parallel \
  --no-configuration-cache \
  -PmavenCentralUsername="可选的中央仓库账号" \
  -PmavenCentralPassword="可选的中央仓库密码" \
  -PsigningInMemoryKeyId="keyId" \
  -PsigningInMemoryKey="-----BEGIN PGP PRIVATE KEY BLOCK----- 类似的原始gpg字符串格式" \
  -PsigningInMemoryKeyPassword="qwer1234"
```

> 可参考 [actions 配置](/.github/workflows/maven-central-publish.yaml)
>
> 之所以此处配置与传统 signing 配置不同，可查看 `com.vanniktech.maven.publish` 插件的 `signAllPublications` 代码
> 插件对此做了特殊处理。

```kotlin
fun signAllPublications() {
  // ...
  // TODO update in memory set up once https://github.com/gradle/gradle/issues/16056 is implemented
  val inMemoryKey = project.providers.gradleProperty("signingInMemoryKey")
  if (inMemoryKey.isPresent) {
    val inMemoryKeyId = project.providers.gradleProperty("signingInMemoryKeyId")
    val inMemoryKeyPassword = project.providers.gradleProperty("signingInMemoryKeyPassword").orElse("")
    project.gradleSigning.useInMemoryPgpKeys(inMemoryKeyId.orNull, inMemoryKey.get(), inMemoryKeyPassword.get())
  }
  // ...
}
```
