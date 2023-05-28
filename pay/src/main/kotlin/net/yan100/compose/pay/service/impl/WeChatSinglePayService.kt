package net.yan100.compose.pay.service.impl

import com.wechat.pay.java.service.payments.jsapi.JsapiService
import com.wechat.pay.java.service.payments.jsapi.model.*
import com.wechat.pay.java.service.refund.RefundService
import com.wechat.pay.java.service.refund.model.AmountReq
import com.wechat.pay.java.service.refund.model.CreateRequest
import net.yan100.compose.core.exceptions.KnownException
import net.yan100.compose.core.exceptions.requireKnown
import net.yan100.compose.core.id.BizCodeGenerator
import net.yan100.compose.core.lang.ISO4217
import net.yan100.compose.core.lang.hasText
import net.yan100.compose.pay.models.request.CreateOrderApiRequestParam
import net.yan100.compose.pay.models.request.FindPayOrderRequestParam
import net.yan100.compose.pay.models.response.CreateOrderApiResponseResult
import net.yan100.compose.pay.models.response.FindPayOrderResponseResult
import net.yan100.compose.pay.properties.WeChatPaySingleConfigProperty
import net.yan100.compose.pay.service.SinglePayService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
//@ConditionalOnBean(value = [JsapiService::class, RefundService::class])
class WeChatSinglePayService(
  private val jsApi: JsapiService,
  private val refundApi: RefundService,
  private val payProperty: WeChatPaySingleConfigProperty,
  private val bigCodeGenerator: BizCodeGenerator
) : SinglePayService {
  companion object {
    private val HUNDRED = BigDecimal("100")
  }

  override fun pullUpMpPayOrder(createOrderRequestParam: CreateOrderApiRequestParam): CreateOrderApiResponseResult? {
    // TODO 此处没有传入 货币单位
    val amount = Amount().apply {
      total = createOrderRequestParam.amount!!.multiply(HUNDRED).toInt()
    }

    val payer = Payer().apply {
      openid = createOrderRequestParam.wechatUserOpenId
    }

    val request = PrepayRequest().apply {
      this.amount = amount
      this.payer = payer
      appid = payProperty.mpAppId
      mchid = payProperty.merchantId
      description = createOrderRequestParam.title
      notifyUrl = payProperty.asyncNotifyUrl
      outTradeNo = createOrderRequestParam.customOrderId
    }

    return jsApi.prepay(request).let { prePayResponse ->
      CreateOrderApiResponseResult().apply {
        prePayId = prePayResponse.prepayId
      }
    }
  }

  override fun findPayOrder(findRq: FindPayOrderRequestParam): FindPayOrderResponseResult? {
    requireKnown(
      !(findRq.merchantOrderId.hasText() && findRq.bizCode.hasText())
    ) { "商户订单号和第三方订单号不能同时为空" }

    val transaction = if (findRq.merchantOrderId.hasText()) {
        jsApi.queryOrderByOutTradeNo(QueryOrderByOutTradeNoRequest().apply {
          outTradeNo = findRq.merchantOrderId
          mchid = payProperty.merchantId
        })
      } else if (findRq.bizCode.hasText()) {
        jsApi.queryOrderById(QueryOrderByIdRequest().apply {
          transactionId = findRq.bizCode
          mchid = payProperty.merchantId
        })
      } else throw KnownException("订单号或商户订单号为空为空")

    return FindPayOrderResponseResult().apply {
      orderId = transaction!!.outTradeNo
      orderNo = transaction.transactionId
      // 金额 / 100
      amount = BigDecimal(transaction.amount.total).setScale(2, RoundingMode.UNNECESSARY).divide(HUNDRED, RoundingMode.UNNECESSARY)
      tradeStatus = transaction.tradeState.toString()
    }
  }

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
