package net.yan100.compose.pay.service.impl

import com.wechat.pay.java.service.payments.jsapi.JsapiService
import com.wechat.pay.java.service.payments.jsapi.model.*
import com.wechat.pay.java.service.payments.model.Transaction
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
import net.yan100.compose.pay.models.response.QueryOrderApiResponseResult
import net.yan100.compose.pay.properties.WeChatPaySingleConfigProperty
import net.yan100.compose.pay.service.SinglePayService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
@ConditionalOnBean(value = [JsapiService::class, RefundService::class])
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

  override fun findPayOrder(findPayOrderRequestParam: FindPayOrderRequestParam): QueryOrderApiResponseResult? {
    requireKnown(
      !(findPayOrderRequestParam.merchantOrderId.hasText() && findPayOrderRequestParam.bizCode.hasText())
    ) { "商户订单号和第三方订单号不能同时为空" }

    val transaction: Transaction? =
      if (findPayOrderRequestParam.merchantOrderId.hasText()) {
        val queryOrderByOutTradeNoRequest = QueryOrderByOutTradeNoRequest()
        queryOrderByOutTradeNoRequest.outTradeNo = findPayOrderRequestParam.merchantOrderId
        queryOrderByOutTradeNoRequest.mchid = payProperty.merchantId
        jsApi.queryOrderByOutTradeNo(queryOrderByOutTradeNoRequest)
      } else if (findPayOrderRequestParam.bizCode.hasText()) {
        val qor = QueryOrderByIdRequest().apply {
          transactionId = findPayOrderRequestParam.bizCode
          mchid = payProperty.merchantId
        }
        jsApi.queryOrderById(qor)
      } else throw KnownException("订单号为空")

    return QueryOrderApiResponseResult().apply {
      orderId = transaction!!.outTradeNo
      orderNo = transaction.transactionId
      money = BigDecimal(transaction.amount.total).setScale(2, RoundingMode.UNNECESSARY).divide(HUNDRED, RoundingMode.UNNECESSARY)
      tradeStatus = transaction.tradeState.toString()
    }
  }

  override fun refundPayOrder(
    refundAmount: BigDecimal,
    totalAmount: BigDecimal,
    currency: ISO4217
  ) {
    val createRequest = CreateRequest()
    val amountReq = AmountReq().apply {
      // 将金额比例乘以 100
      val totalLongBy = (totalAmount * HUNDRED).longValueExact()
      val refundLongBy = (refundAmount * HUNDRED).longValueExact()
      this.currency = ISO4217.CNY.getValue()
      refund = refundLongBy
      total = totalLongBy
    }

    createRequest.apply {
      amount = amountReq
      outTradeNo = "1649768373464600576"
      outRefundNo = bigCodeGenerator.nextCodeStr()
    }
    // TODO 此处空返回
    val refundDetails = refundApi.create(createRequest)
  }
}
