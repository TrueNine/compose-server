## .gradle gradle.properties

```properties
repositories.yunxiao=#
credentials.yunxiao.username=#
credentials.yunxiao.password=#

signing.keyId=#
signing.password=#
signing.secretKeyRingFile=#

systemProp.http.proxyHost=127.0.0.1
systemProp.http.proxyPort=7899
systemProp.https.proxyHost=127.0.0.1
systemProp.https.proxyPort=7899

org.gradle.configuration-cache=false
org.gradle.configuration-cache.problems=warn
org.gradle.configureondemand=true
org.gradle.caching=true
org.gradle.workers.max=12
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.jvmargs=-Xmx2g -XX:MaxMetaspaceSize=512m -XX:+UseParallelGC -Dfile.encoding=UTF-8
kapt.use.k2=true
dotenv=#

kotlin.code.style=official
kotlin.incremental=true
kotlin.daemon.jvmargs=-Xmx1g

```
