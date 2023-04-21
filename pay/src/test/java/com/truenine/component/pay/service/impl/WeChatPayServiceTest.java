package com.truenine.component.pay.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.truenine.component.core.lang.LogKt;
import com.truenine.component.pay.Application;
import com.truenine.component.pay.models.request.CreateOrderRequestParam;
import com.truenine.component.pay.models.response.CreateOrderResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.math.BigDecimal;

@Slf4j
@SpringBootTest(classes = Application.class)
public class WeChatPayServiceTest extends AbstractTestNGSpringContextTests {

  @Autowired
  private WeChatPayService weChatPayService;

  @Test
  void testCreateOrder() {
    Snowflake snowflake = IdUtil.getSnowflake();
    CreateOrderRequestParam createOrderRequestParam = new CreateOrderRequestParam();
    createOrderRequestParam.setOrderId(snowflake.nextIdStr());
    createOrderRequestParam.setOpenId("oRYYL5H-IKKK0sHs1L0EOjZw1Ne4");
    createOrderRequestParam.setMoney(new BigDecimal("0.01"));
    createOrderRequestParam.setTitle("一斤菠萝");

    CreateOrderResponseResult order = weChatPayService.createOrder(createOrderRequestParam);
    log.debug(order.toString());
  }

}
