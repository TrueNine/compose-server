package net.yan100.compose.pay.service.impl

import com.wechat.pay.java.service.payments.jsapi.JsapiService
import com.wechat.pay.java.service.payments.jsapi.model.*
import com.wechat.pay.java.service.refund.RefundService
import com.wechat.pay.java.service.refund.model.AmountReq
import com.wechat.pay.java.service.refund.model.CreateRequest
import jakarta.validation.Valid
import net.yan100.compose.core.encrypt.EncryptAlgorithmTyping
import net.yan100.compose.core.encrypt.Encryptors
import net.yan100.compose.core.encrypt.Keys
import net.yan100.compose.core.exceptions.KnownException
import net.yan100.compose.core.exceptions.requireKnown
import net.yan100.compose.core.id.BizCodeGenerator
import net.yan100.compose.core.lang.ISO4217
import net.yan100.compose.core.lang.encodeBase64String
import net.yan100.compose.core.lang.hasText
import net.yan100.compose.core.lang.iso8601LongUtc
import net.yan100.compose.pay.models.req.CreateMpPayOrderReq
import net.yan100.compose.pay.models.req.FindPayOrderReq
import net.yan100.compose.pay.models.resp.CreateMpPayOrderResp
import net.yan100.compose.pay.models.resp.FindPayOrderResp
import net.yan100.compose.pay.properties.WeChatPaySingleConfigProperty
import net.yan100.compose.pay.service.SinglePayService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@ConditionalOnBean(value = [JsapiService::class, RefundService::class])
class WeChatSinglePayService(
  private val wechatJsService: JsapiService,
  private val refundApi: RefundService,
  private val payProperty: WeChatPaySingleConfigProperty,
  private val bigCodeGenerator: BizCodeGenerator
) : SinglePayService {
  companion object {
    @JvmStatic
    private val HUNDRED = BigDecimal("100")
  }

  override fun createMpPayOrder(@Valid req: CreateMpPayOrderReq): CreateMpPayOrderResp {
    val amount = Amount().apply {
      currency = req.currency.getValue()
      total = req.amount!!.multiply(HUNDRED).toInt()
    }
    val payer = Payer().apply {
      openid = req.wechatUserOpenId
    }

    val request = PrepayRequest().apply {
      this.amount = amount
      this.payer = payer
      outTradeNo = req.customOrderId
      description = req.title
      appid = payProperty.mpAppId
      mchid = payProperty.merchantId
      notifyUrl = payProperty.asyncNotifyUrl
    }

    val prePay = wechatJsService.prepay(request)
    return CreateMpPayOrderResp().apply {
      random32String = Keys.generateRandomAsciiString(32)
      prePayId = prePay?.prepayId
      isIso8601Second = LocalDateTime.now().iso8601LongUtc.toString()
      signType = EncryptAlgorithmTyping.RSA.getValue()
      // 签名
      val signatureStr = "${payProperty.mpAppId}\n${isIso8601Second}\n${random32String}\n${prePayId}\n"
      val signature = Encryptors.signWithSha256WithRsaByRsaPrivateKey(
        signatureStr,
        Keys.readRsaPrivateKeyByBase64AndStandard(payProperty.privateKey)!!
      )
      paySign = signature.sign().encodeBase64String
    }
  }

  override fun findPayOrder(findRq: FindPayOrderReq): FindPayOrderResp? {
    requireKnown(
      !(findRq.merchantOrderId.hasText() && findRq.bizCode.hasText())
    ) { "商户订单号和第三方订单号不能同时为空" }

    val transaction = if (findRq.merchantOrderId.hasText()) {
      wechatJsService.queryOrderByOutTradeNo(QueryOrderByOutTradeNoRequest().apply {
        outTradeNo = findRq.merchantOrderId
        mchid = payProperty.merchantId
      })
    } else if (findRq.bizCode.hasText()) {
      wechatJsService.queryOrderById(QueryOrderByIdRequest().apply {
        transactionId = findRq.bizCode
        mchid = payProperty.merchantId
      })
    } else throw KnownException("订单号或商户订单号为空为空")

    return FindPayOrderResp().apply {
      meta = transaction
      customOrderId = transaction!!.outTradeNo
      orderNumber = transaction.transactionId
      amount = BigDecimal(transaction.amount.total).setScale(2, RoundingMode.UNNECESSARY).divide(HUNDRED, RoundingMode.UNNECESSARY)
      tradeStatus = transaction.tradeState.toString()
      tradeStatusDesc = transaction.tradeStateDesc
      // TODO 可能会出现转换问题，注意观测
      paySuccessDatetime = LocalDateTime.parse(
        transaction.successTime,
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
      )
    }
  }

  @Deprecated(message = "暂时不可用")
  override fun applyRefundPayOrder(
    refundAmount: BigDecimal,
    totalAmount: BigDecimal,
    currency: ISO4217
  ) {
    val createRequest = CreateRequest()
    val amountReq = AmountReq().apply {
      // 将金额比例乘以 100
      val totalLongBy = (totalAmount * HUNDRED).longValueExact()
      val refundLongBy = (refundAmount * HUNDRED).longValueExact()
      this.currency = currency.getValue()
      refund = refundLongBy
      total = totalLongBy
    }

    createRequest.apply {
      amount = amountReq
      // 商户订单号
      outTradeNo = bigCodeGenerator.nextCodeStr()
      outRefundNo = bigCodeGenerator.nextCodeStr()
    }
    // TODO 此处空返回
    val refundDetails = refundApi.create(createRequest)
  }
}
