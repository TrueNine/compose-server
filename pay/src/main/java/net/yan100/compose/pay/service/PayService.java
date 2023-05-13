package net.yan100.compose.pay.service;

import net.yan100.compose.pay.api.model.request.CreateOrderApiRequestParam;
import net.yan100.compose.pay.api.model.request.QueryOrderApiRequestParam;
import net.yan100.compose.pay.api.model.response.CreateOrderApiResponseResult;
import net.yan100.compose.pay.api.model.response.QueryOrderApiResponseResult;

public interface PayService {

  CreateOrderApiResponseResult createOrder(CreateOrderApiRequestParam createOrderRequestParam);

  QueryOrderApiResponseResult queryOrder(QueryOrderApiRequestParam queryOrderApiRequestParam);

}
