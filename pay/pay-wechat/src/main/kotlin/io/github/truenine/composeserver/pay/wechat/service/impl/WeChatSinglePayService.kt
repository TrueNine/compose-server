package io.github.truenine.composeserver.pay.wechat.service.impl

import com.wechat.pay.java.core.RSAAutoCertificateConfig
import com.wechat.pay.java.core.notification.NotificationParser
import com.wechat.pay.java.core.notification.RequestParam
import com.wechat.pay.java.service.partnerpayments.app.model.Transaction
import com.wechat.pay.java.service.payments.jsapi.JsapiService
import com.wechat.pay.java.service.payments.jsapi.model.Amount
import com.wechat.pay.java.service.payments.jsapi.model.Payer
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByIdRequest
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest
import com.wechat.pay.java.service.refund.RefundService
import com.wechat.pay.java.service.refund.model.AmountReq
import com.wechat.pay.java.service.refund.model.CreateRequest
import io.github.truenine.composeserver.enums.EncryptAlgorithm
import io.github.truenine.composeserver.enums.ISO4217
import io.github.truenine.composeserver.generator.IOrderCodeGenerator
import io.github.truenine.composeserver.hasText
import io.github.truenine.composeserver.iso8601LongUtc
import io.github.truenine.composeserver.pay.FindPayOrderDto
import io.github.truenine.composeserver.pay.FindPayOrderVo
import io.github.truenine.composeserver.pay.PaySuccessNotifyVo
import io.github.truenine.composeserver.pay.SinglePayService
import io.github.truenine.composeserver.pay.wechat.properties.WeChatPaySingleConfigProperty
import io.github.truenine.composeserver.security.crypto.CryptographicKeyManager
import io.github.truenine.composeserver.security.crypto.CryptographicOperations
import io.github.truenine.composeserver.security.crypto.encodeBase64String
import io.github.truenine.composeserver.slf4j
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper

@Service
class WeChatSinglePayService(
  private val wechatJsService: JsapiService,
  private val refundApi: RefundService,
  private val payProperty: WeChatPaySingleConfigProperty,
  private val bigCodeGenerator: IOrderCodeGenerator,
  private val rsaConfig: RSAAutoCertificateConfig,
  private val mapper: ObjectMapper,
) : SinglePayService {
  companion object {
    @JvmStatic private val HUNDRED = BigDecimal("100")
    private val log = slf4j(WeChatSinglePayService::class)
  }

  override fun createMpPayOrder(req: SinglePayService.CreateMpPayDto): SinglePayService.CreateMpPayVo {
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
    return SinglePayService.CreateMpPayVo(
        random32String = CryptographicKeyManager.generateRandomAsciiString(32),
        iso8601Second = LocalDateTime.now().iso8601LongUtc.toString(),
        signType = EncryptAlgorithm.RSA.value,
      )
      .apply {
        prePayId = prePay?.prepayId
        // Signature
        val signatureStr = "${payProperty.mpAppId}\n${iso8601Second}\n${random32String}\n${prePayId}\n"
        val signature =
          CryptographicOperations.signWithSha256WithRsaByRsaPrivateKey(
            signatureStr,
            CryptographicKeyManager.readRsaPrivateKeyByBase64AndStandard(payProperty.privateKey)!!,
          )
        paySign = signature.sign().encodeBase64String
      }
  }

  override fun findPayOrder(findRq: FindPayOrderDto): FindPayOrderVo? {
    require(findRq.merchantOrderId.hasText() || findRq.bizCode.hasText()) { "Merchant order ID and third-party order ID cannot both be empty" }

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
      } else error("Order number or merchant order ID is empty")

    return FindPayOrderVo(
      meta = transaction,
      customOrderId = transaction!!.outTradeNo,
      orderNumber = transaction.transactionId,
      amount = BigDecimal(transaction.amount.total).setScale(2, RoundingMode.UNNECESSARY).divide(HUNDRED, RoundingMode.UNNECESSARY),
      tradeStatus = transaction.tradeState.toString(),
      tradeStatusDesc = transaction.tradeStateDesc,
      paySuccessDatetime = LocalDateTime.parse(transaction.successTime, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")),
    )
  }

  @Deprecated(message = "Temporarily unavailable")
  override fun applyRefundPayOrder(refundAmount: BigDecimal, totalAmount: BigDecimal, currency: ISO4217) {
    val createRequest = CreateRequest()
    val amountReq =
      AmountReq().apply {
        // Multiply amount by 100 to convert to minor units
        val totalLongBy = (totalAmount * HUNDRED).longValueExact()
        val refundLongBy = (refundAmount * HUNDRED).longValueExact()
        this.currency = currency.value
        refund = refundLongBy
        total = totalLongBy
      }

    createRequest.apply {
      amount = amountReq
      // Merchant order number
      outTradeNo = bigCodeGenerator.nextString()
      outRefundNo = bigCodeGenerator.nextString()
    }
    // TODO Currently returns nothing here
    refundApi.create(createRequest)
  }

  override fun receivePayNotify(
    metaData: String,
    request: HttpServletRequest,
    response: HttpServletResponse,
    lazyCall: (successReq: PaySuccessNotifyVo) -> Unit,
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

    val transaction = NotificationParser(rsaConfig).parse(requestParam, Transaction::class.java)
    val r =
      if (transaction.tradeState == Transaction.TradeStateEnum.SUCCESS) {
        PaySuccessNotifyVo(
          payCode = transaction.transactionId,
          orderCode = transaction.outTradeNo,
          currency = ISO4217[transaction.amount.currency],
          meta = mapper.writeValueAsString(transaction),
        )
      } else null
    try {
      lazyCall(r!!)
    } catch (e: Exception) {
      response.status = 400
      log.error("A payment exception occurred and has been caught and returned to WeChat", e)
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
