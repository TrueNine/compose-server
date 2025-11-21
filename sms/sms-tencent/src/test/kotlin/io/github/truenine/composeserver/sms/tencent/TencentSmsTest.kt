package io.github.truenine.composeserver.sms.tencent

import com.tencentcloudapi.common.Credential
import com.tencentcloudapi.common.profile.ClientProfile
import com.tencentcloudapi.common.profile.Language
import com.tencentcloudapi.sms.v20210111.SmsClient
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TencentSmsTest {

  @Ignore
  @Test
  fun `tencent cloud sms send failed`() {
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
    req.signName = "Hunan Mucan Information Technology Co., Ltd."
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
      assertNotEquals(it.code, "FailedOperation.SignatureIncorrectOrUnapproved", "Signature is incorrect or not approved")
      assertContains(it.code, "Ok")
    }
  }
}
