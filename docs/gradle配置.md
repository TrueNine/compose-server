## .gradle gradle.properties

```properties
repositories.yunxiao=#
credentials.yunxiao.username=#
credentials.yunxiao.password=#
credentials.sonatype.username=#
credentials.sonatype.password=#
# 切记 signing.keyId 等字段的尾随空格问题
signing.keyId=#
signing.password=#
signing.secretKeyRingFile=#
systemProp.http.proxyHost=127.0.0.1
systemProp.http.proxyPort=7899
systemProp.https.proxyHost=127.0.0.1
systemProp.https.proxyPort=7899
org.gradle.configuration-cache=false
org.gradle.configuration-cache.problems=warn
org.gradle.configureondemand=false
org.gradle.caching=false
org.gradle.workers.max=5
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.jvmargs=-Xmx2g -XX:MaxMetaspaceSize=512m -XX:+UseParallelGC -Dfile.encoding=UTF-8
org.gradle.console=plain
org.gradle.logging.level=warn
kapt.use.k2=true
# 本地 .env 配置文件
dotenv=#
org.jetbrains.dokka.experimental.gradle.pluginMode=V2Enabled
kotlin.code.style=official
kotlin.incremental=true
kotlin.daemon.jvmargs=-Xmx1g

```
