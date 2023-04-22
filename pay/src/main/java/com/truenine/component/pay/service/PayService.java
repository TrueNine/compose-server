package com.truenine.component.pay.service;

import com.truenine.component.pay.api.model.request.CreateOrderApiRequestParam;
import com.truenine.component.pay.api.model.request.QueryOrderApiRequestParam;
import com.truenine.component.pay.api.model.response.CreateOrderApiResponseResult;
import com.truenine.component.pay.api.model.response.QueryOrderApiResponseResult;

public interface PayService {

  CreateOrderApiResponseResult createOrder(CreateOrderApiRequestParam createOrderRequestParam);
  QueryOrderApiResponseResult queryOrder(QueryOrderApiRequestParam queryOrderApiRequestParam);

}
