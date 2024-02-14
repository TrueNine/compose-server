package net.yan100.compose.oss.aliyun

import com.aliyun.oss.OSSClientBuilder


class ConnectTest {

    //TODO 继续测试
    fun connect() {
        val endpoint = "oss-cn-shenzhen.aliyuncs.com"
        val accessKeyId = "LTAI5tRUDCxxi9QusF5w6YMG"
        val accessKeySecret = "rmU482bQLrh7bkQbSXGin8U8h8DAXY"
        val ossClient = OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret)
        ossClient.listBuckets().forEach {
            it.extranetEndpoint
        }
        ossClient.shutdown()
    }
}
