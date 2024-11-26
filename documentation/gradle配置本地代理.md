## gradle 配置代理

在 gradle.properties 中添加以下配置，全局则为全部，单独就是单独

```properties
systemProp.http.proxyHost=127.0.0.1
systemProp.http.proxyPort=10809
systemProp.https.proxyHost=127.0.0.1
systemProp.https.proxyPort=10809
```
