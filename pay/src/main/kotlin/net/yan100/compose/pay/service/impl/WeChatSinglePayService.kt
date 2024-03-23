/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.pay.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.wechat.pay.java.core.RSAAutoCertificateConfig
import com.wechat.pay.java.core.notification.NotificationParser
import com.wechat.pay.java.core.notification.RequestParam
import com.wechat.pay.java.service.partnerpayments.app.model.Transaction
import com.wechat.pay.java.service.payments.jsapi.JsapiService
import com.wechat.pay.java.service.payments.jsapi.model.*
import com.wechat.pay.java.service.refund.RefundService
import com.wechat.pay.java.service.refund.model.AmountReq
import com.wechat.pay.java.service.refund.model.CreateRequest
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import net.yan100.compose.core.IBizCodeGenerator
import net.yan100.compose.core.exceptions.KnownException
import net.yan100.compose.core.exceptions.requireKnown
import net.yan100.compose.core.extensionfunctions.encodeBase64String
import net.yan100.compose.core.extensionfunctions.hasText
import net.yan100.compose.core.extensionfunctions.iso8601LongUtc
import net.yan100.compose.core.typing.EncryptAlgorithmTyping
import net.yan100.compose.core.typing.ISO4217
import net.yan100.compose.core.util.encrypt.Encryptors
import net.yan100.compose.core.util.encrypt.Keys
import net.yan100.compose.pay.models.req.CreateMpPayOrderReq
import net.yan100.compose.pay.models.req.FindPayOrderReq
import net.yan100.compose.pay.models.resp.CreateMpPayOrderResp
import net.yan100.compose.pay.models.resp.FindPayOrderResp
import net.yan100.compose.pay.models.resp.PaySuccessNotifyResp
import net.yan100.compose.pay.properties.WeChatPaySingleConfigProperty
import net.yan100.compose.pay.service.SinglePayService
import org.springframework.stereotype.Service

@Service
class WeChatSinglePayService(
  private val wechatJsService: JsapiService,
  private val refundApi: RefundService,
  private val payProperty: WeChatPaySingleConfigProperty,
  private val bigCodeGenerator: IBizCodeGenerator,
  private val rsaConfig: RSAAutoCertificateConfig,
  private val mapper: ObjectMapper,
) : SinglePayService {
  companion object {
    @JvmStatic private val HUNDRED = BigDecimal("100")
    private val log = net.yan100.compose.core.log.slf4j(WeChatSinglePayService::class)
  }

  override fun createMpPayOrder(@Valid req: CreateMpPayOrderReq): CreateMpPayOrderResp {
    val amount =
      Amount().apply {
        currency = req.currency.value
        total = req.amount!!.multiply(HUNDRED).toInt()
      }
    val payer = Payer().apply { openid = req.wechatUserOpenId }

    val request =
      PrepayRequest().apply {
        this.amount = amount
        this.payer = payer
        outTradeNo = req.customOrderId
        description = req.title
        appid = payProperty.mpAppId
        mchid = payProperty.merchantId
        notifyUrl = payProperty.asyncSuccessNotifyUrl
      }

    val prePay = wechatJsService.prepay(request)
    return CreateMpPayOrderResp().apply {
      random32String = Keys.generateRandomAsciiString(32)
      prePayId = prePay?.prepayId
      iso8601Second = LocalDateTime.now().iso8601LongUtc.toString()
      signType = EncryptAlgorithmTyping.RSA.value
      // 签名
      val signatureStr = "${payProperty.mpAppId}\n${iso8601Second}\n${random32String}\n${prePayId}\n"
      val signature = Encryptors.signWithSha256WithRsaByRsaPrivateKey(signatureStr, Keys.readRsaPrivateKeyByBase64AndStandard(payProperty.privateKey)!!)
      paySign = signature.sign().encodeBase64String
    }
  }

  override fun findPayOrder(findRq: FindPayOrderReq): FindPayOrderResp? {
    requireKnown(!(findRq.merchantOrderId.hasText() && findRq.bizCode.hasText())) { "商户订单号和第三方订单号不能同时为空" }

    val transaction =
      if (findRq.merchantOrderId.hasText()) {
        wechatJsService.queryOrderByOutTradeNo(
          QueryOrderByOutTradeNoRequest().apply {
            outTradeNo = findRq.merchantOrderId
            mchid = payProperty.merchantId
          }
        )
      } else if (findRq.bizCode.hasText()) {
        wechatJsService.queryOrderById(
          QueryOrderByIdRequest().apply {
            transactionId = findRq.bizCode
            mchid = payProperty.merchantId
          }
        )
      } else throw KnownException("订单号或商户订单号为空为空")

    return FindPayOrderResp().apply {
      meta = transaction
      customOrderId = transaction!!.outTradeNo
      orderNumber = transaction.transactionId
      amount = BigDecimal(transaction.amount.total).setScale(2, RoundingMode.UNNECESSARY).divide(HUNDRED, RoundingMode.UNNECESSARY)
      tradeStatus = transaction.tradeState.toString()
      tradeStatusDesc = transaction.tradeStateDesc

      paySuccessDatetime = LocalDateTime.parse(transaction.successTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"))
    }
  }

  @Deprecated(message = "暂时不可用")
  override fun applyRefundPayOrder(
    refundAmount: BigDecimal,
    totalAmount: BigDecimal,
    currency: ISO4217,
  ) {
    val createRequest = CreateRequest()
    val amountReq =
      AmountReq().apply {
        // 将金额比例乘以 100
        val totalLongBy = (totalAmount * HUNDRED).longValueExact()
        val refundLongBy = (refundAmount * HUNDRED).longValueExact()
        this.currency = currency.value
        refund = refundLongBy
        total = totalLongBy
      }

    createRequest.apply {
      amount = amountReq
      // 商户订单号
      outTradeNo = bigCodeGenerator.nextString()
      outRefundNo = bigCodeGenerator.nextString()
    }
    // TODO 此处空返回
    val refundDetails = refundApi.create(createRequest)
  }

  override fun receivePayNotify(
    metaData: String,
    request: HttpServletRequest,
    response: HttpServletResponse,
    lazyCall: (successReq: PaySuccessNotifyResp) -> Unit,
  ): String? {
    val headersMap = request.headerNames.asSequence().map { it to request.getHeader(it) }.toMap()
    val requestParam =
      RequestParam.Builder()
        .serialNumber(headersMap["Wechatpay-Serial"])
        .signType(headersMap["Wechatpay-Signature-Type"])
        .nonce(headersMap["Wechatpay-Nonce"])
        .signature(headersMap["Wechatpay-Signature"])
        .timestamp(headersMap["Wechatpay-Timestamp"])
        .body(metaData)
        .build()

    val transaction = NotificationParser(rsaConfig).let { parser -> parser.parse(requestParam, Transaction::class.java) }
    val r =
      if (transaction.tradeState == Transaction.TradeStateEnum.SUCCESS) {
        PaySuccessNotifyResp().apply {
          payCode = transaction.transactionId
          orderCode = transaction.outTradeNo
          currency = ISO4217.findVal(transaction.amount.currency)
          meta = mapper.writeValueAsString(transaction)
        }
      } else null
    try {
      lazyCall(r!!)
    } catch (e: Exception) {
      response.status = 400
      log.error("发生支付异常，已被捕获并返回微信", e)
      return """
        {  
          "code": "FAIL",
          "message": "${e.message}"
        }
      """
        .trimIndent()
    }
    return ""
  }
}
