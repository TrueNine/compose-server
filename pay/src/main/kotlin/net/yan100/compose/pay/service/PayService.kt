package net.yan100.compose.pay.service

import net.yan100.compose.pay.models.request.CreateOrderApiRequestParam
import net.yan100.compose.pay.models.request.QueryOrderApiRequestParam
import net.yan100.compose.pay.models.response.CreateOrderApiResponseResult
import net.yan100.compose.pay.models.response.QueryOrderApiResponseResult

interface PayService {
  fun createOrder(createOrderRequestParam: CreateOrderApiRequestParam): CreateOrderApiResponseResult?
  fun queryOrder(queryOrderApiRequestParam: QueryOrderApiRequestParam): QueryOrderApiResponseResult?
}
