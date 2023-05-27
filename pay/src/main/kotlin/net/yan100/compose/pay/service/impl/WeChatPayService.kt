package net.yan100.compose.pay.service.impl

import com.wechat.pay.java.service.payments.jsapi.JsapiService
import com.wechat.pay.java.service.payments.jsapi.model.*
import com.wechat.pay.java.service.payments.model.Transaction
import com.wechat.pay.java.service.refund.RefundService
import com.wechat.pay.java.service.refund.model.AmountReq
import com.wechat.pay.java.service.refund.model.CreateRequest
import net.yan100.compose.core.exceptions.KnownException
import net.yan100.compose.core.id.BizCodeGenerator
import net.yan100.compose.core.lang.Str
import net.yan100.compose.core.lang.hasText
import net.yan100.compose.pay.models.request.CreateOrderApiRequestParam
import net.yan100.compose.pay.models.request.QueryOrderApiRequestParam
import net.yan100.compose.pay.models.response.CreateOrderApiResponseResult
import net.yan100.compose.pay.models.response.QueryOrderApiResponseResult
import net.yan100.compose.pay.properties.WeChatProperties
import net.yan100.compose.pay.service.PayService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class WeChatPayService(
  private val jsApi: JsapiService,
  private val refundApi: RefundService,
  private val weChatProperties: WeChatProperties,
  private val bigCodeGenerator: BizCodeGenerator
) : PayService {
  companion object {
    private val HUNDRED = BigDecimal("100")
  }

  override fun createOrder(createOrderRequestParam: CreateOrderApiRequestParam): CreateOrderApiResponseResult? {
    val request = PrepayRequest()
    val amount = Amount()
    amount.total = createOrderRequestParam.money!!.multiply(HUNDRED).toInt()
    request.amount = amount
    request.appid = weChatProperties.appId
    request.mchid = weChatProperties.merchantId
    request.description = createOrderRequestParam.title
    request.notifyUrl = weChatProperties.notifyUrl
    request.outTradeNo = createOrderRequestParam.orderId
    val payer = Payer()
    payer.openid = createOrderRequestParam.openId
    request.payer = payer
    val response = jsApi.prepay(request)
    val createOrderApiResponseResult = CreateOrderApiResponseResult()
    createOrderApiResponseResult.prepayId = response.prepayId
    return createOrderApiResponseResult
  }

  override fun queryOrder(queryOrderApiRequestParam: QueryOrderApiRequestParam): QueryOrderApiResponseResult? {
    require(
      !(queryOrderApiRequestParam.orderId.hasText() && queryOrderApiRequestParam.orderNo.hasText())
    ) { "商户订单号和第三方订单号不能同时为空" }

    val transaction: Transaction? =
      if (queryOrderApiRequestParam.orderId.hasText()) {
        val queryOrderByOutTradeNoRequest = QueryOrderByOutTradeNoRequest()
        queryOrderByOutTradeNoRequest.outTradeNo = queryOrderApiRequestParam.orderId
        queryOrderByOutTradeNoRequest.mchid = weChatProperties.merchantId
        jsApi.queryOrderByOutTradeNo(queryOrderByOutTradeNoRequest)
      } else if (Str.hasText(queryOrderApiRequestParam.orderNo)) {
        val qor = QueryOrderByIdRequest()
        qor.transactionId = queryOrderApiRequestParam.orderNo
        qor.mchid = weChatProperties.merchantId
        jsApi.queryOrderById(qor)
      } else {
        throw KnownException("订单号为空")
      }

    return QueryOrderApiResponseResult().apply {
      orderId = transaction!!.outTradeNo
      orderNo = transaction.transactionId
      money = BigDecimal(transaction.amount.total).setScale(2, RoundingMode.UNNECESSARY).divide(HUNDRED, RoundingMode.UNNECESSARY)
      tradeStatus = transaction.tradeState.toString()
    }
  }

  fun refundOrder() {
    val createRequest = CreateRequest()
    val amountReq = AmountReq().apply {
      val amount = BigDecimal("0.01").multiply(HUNDRED).toLong()
      refund = amount
      total = amount
      currency = "CNY"
    }
    createRequest.apply {
      amount = amountReq
      outTradeNo = "1649768373464600576"
      outRefundNo = bigCodeGenerator.nextCodeStr()
    }
    refundApi.create(createRequest)
  }
}
