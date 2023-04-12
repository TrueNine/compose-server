package com.truenine.component.pay.service;

import com.truenine.component.pay.models.request.CreateOrderRequestParam;
import com.truenine.component.pay.models.response.CreateOrderResponseResult;

public interface PayService {

  CreateOrderResponseResult createOrder(CreateOrderRequestParam createOrderRequestParam);

}
