package io.github.truenine.composeserver.sms.tencent

import com.tencentcloudapi.common.Credential
import com.tencentcloudapi.common.profile.ClientProfile
import com.tencentcloudapi.common.profile.Language
import com.tencentcloudapi.sms.v20210111.SmsClient
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse
import kotlin.test.*

class TencentSmsTest {

  @Ignore
  @Test
  fun `腾讯云发送短信 太垃圾了，没发出去`() {
    val sdkAppId = System.getenv("TENCENTCLOUD_SDK_APP_ID")
    val secretId = System.getenv("TENCENTCLOUD_SECRET_ID")
    val secretKey = System.getenv("TENCENTCLOUD_SECRET_KEY")

    assertNotNull(sdkAppId)
    assertNotNull(secretId)
    assertNotNull(secretKey)

    val cred = Credential(secretId, secretKey)

    val clientProfile = ClientProfile()
    clientProfile.language = Language.ZH_CN
    clientProfile.isDebug = true

    val client = SmsClient(cred, "ap-beijing", clientProfile)

    val req = SendSmsRequest()
    req.smsSdkAppId = sdkAppId
    req.signName = "湖南募残信息科技"
    req.templateId = "2373465"
    req.templateParamSet = arrayOf("134235")
    req.phoneNumberSet = arrayOf("+8619918540858")

    val res = client.SendSms(req)
    println(SendSmsResponse.toJsonString(res))

    val statusSet = res.sendStatusSet
    assertNotNull(statusSet)
    assertTrue { statusSet.size > 0 }
    assertNotNull(res.requestId)
    statusSet.forEach {
      assertNotEquals(it.code, "FailedOperation.SignatureIncorrectOrUnapproved", "签名不正确或未审核通过")
      assertContains(it.code, "Ok")
    }
  }
}
