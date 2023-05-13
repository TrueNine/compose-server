package net.yan100.compose.pay.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.wechat.pay.java.core.util.PemUtil;
import lombok.SneakyThrows;
import net.yan100.compose.pay.api.WeChatApi;
import net.yan100.compose.pay.api.model.request.CreateOrderApiRequestParam;
import net.yan100.compose.pay.api.model.request.QueryOrderApiRequestParam;
import net.yan100.compose.pay.api.model.response.CreateOrderApiResponseResult;
import net.yan100.compose.pay.api.model.response.QueryOrderApiResponseResult;
import net.yan100.compose.pay.models.response.CreateOrderResponse;
import net.yan100.compose.pay.properties.WeChatProperties;
import net.yan100.compose.pay.service.impl.WeChatPayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/v1/wechat")
public class WeChatController {

  private final WeChatApi weChatApi;

  private final WeChatProperties weChatProperties;

  private final WeChatPayService weChatPayService;

  public WeChatController(WeChatApi weChatApi, WeChatProperties weChatProperties, WeChatPayService weChatPayService) {
    this.weChatApi = weChatApi;
    this.weChatProperties = weChatProperties;
    this.weChatPayService = weChatPayService;
  }

  @GetMapping(value = "/getUserInfo")
  public String getUserInfo(String code) {
    return weChatApi.token(weChatProperties.getAppId(), weChatProperties.getAppSecret(), code, "authorization_code").getBody();
  }

  @SneakyThrows
  @GetMapping(value = "/createOrder")
  public CreateOrderResponse createOrder() {
    Snowflake snowflake = IdUtil.getSnowflake();
    CreateOrderApiRequestParam createOrderRequestParam = new CreateOrderApiRequestParam();
    createOrderRequestParam.setOrderId(snowflake.nextIdStr());
    createOrderRequestParam.setOpenId("oRYYL5H-IKKK0sHs1L0EOjZw1Ne4");
    createOrderRequestParam.setMoney(new BigDecimal("0.01"));
    createOrderRequestParam.setTitle("一斤菠萝");

    CreateOrderApiResponseResult createOrderApiResponseResult = weChatPayService.createOrder(createOrderRequestParam);

    CreateOrderResponse createOrderResponse = new CreateOrderResponse();
    createOrderResponse.setNonceStr(RandomUtil.randomString(32));
    createOrderResponse.setPackageStr("prepay_id=" + createOrderApiResponseResult.getPrepayId());
    createOrderResponse.setTimeStamp(String.valueOf(System.currentTimeMillis() / 1000));

    String signatureStr = Stream.of(weChatProperties.getAppId(), createOrderResponse.getTimeStamp(), createOrderResponse.getNonceStr(), createOrderResponse.getPackageStr())
      .collect(Collectors.joining("\n", "", "\n"));

    String privateKey = FileUtil.readString(weChatProperties.getPrivateKeyPath(), StandardCharsets.UTF_8);
    Signature signature = Signature.getInstance("SHA256withRSA");
    signature.initSign(PemUtil.loadPrivateKeyFromString(privateKey));
    signature.update(signatureStr.getBytes(StandardCharsets.UTF_8));

//    String sign = Encryptors.encryptByRsaPublicKeyBase64(privateKey, dataStr, 245, StandardCharsets.UTF_8);
    createOrderResponse.setSignType("RSA");
    createOrderResponse.setPaySign(Base64.encode(signature.sign()));
    return createOrderResponse;
  }

  @GetMapping(value = "/queryOrder")
  public QueryOrderApiResponseResult queryOrder(QueryOrderApiRequestParam queryOrderApiRequestParam) {
    return weChatPayService.queryOrder(queryOrderApiRequestParam);
  }

  @PostMapping(value = "/refundOrder")
  public String refundOrder() {
    weChatPayService.refundOrder();
    return "";
  }

}
