package pers.prover07.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pers.prover07.yygh.common.util.exception.BaseServiceException;
import pers.prover07.yygh.common.util.result.ResultCodeEnum;
import pers.prover07.yygh.enums.PaymentStatusEnum;
import pers.prover07.yygh.enums.PaymentTypeEnum;
import pers.prover07.yygh.enums.RefundStatusEnum;
import pers.prover07.yygh.model.order.OrderInfo;
import pers.prover07.yygh.model.order.PaymentInfo;
import pers.prover07.yygh.model.order.RefundInfo;
import pers.prover07.yygh.order.service.OrderService;
import pers.prover07.yygh.order.service.PaymentInfoService;
import pers.prover07.yygh.order.service.RefundService;
import pers.prover07.yygh.order.service.WxPayService;
import pers.prover07.yygh.order.utils.HttpClient;
import pers.prover07.yygh.order.utils.WxPayInfoUtil;
import pers.prover07.yygh.service.util.constrant.RedisKeyConstant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author by Prover07
 * @classname WxPayServiceImpl
 * @description TODO
 * @date 2021/12/13 11:05
 */
@Service
public class WxPayServiceImpl implements WxPayService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private RefundService refundService;

    @Override
    public Map createNative(String orderId) throws Exception {
        // 判断 redis 中是否存在数据
        String resultStr = (String) redisTemplate.opsForValue().get(RedisKeyConstant.ORDER_WX_NATIVE + orderId);
        if (!StringUtils.isBlank(resultStr)) {
            return JSONObject.parseObject(resultStr);
        }
        OrderInfo orderInfo = orderService.getById(orderId);
        // 添加 paymentInfo 数据
        paymentInfoService.addPaymentInfo(orderInfo, PaymentTypeEnum.WEIXIN.getStatus());
        // 填充获取微信支付信息需要的参数
        HashMap requestMap = new HashMap<String, Object>() {{
            // 设置公众号标识
            put("appid", WxPayInfoUtil.appid);
            // 设置商户号
            put("mch_id", WxPayInfoUtil.partner);
            // 随机字符串，保证签名的不可预测性
            put("nonce_str", WXPayUtil.generateNonceStr());
            // 商品描述
            put("body", orderInfo.getReserveDate() + " - 就诊" + orderInfo.getDepname());
            // 订单流水号
            put("out_trade_no", orderInfo.getOutTradeNo());
            //paramMap.put("total_fee", order.getAmount().multiply(new BigDecimal("100")).longValue()+"");
            // 交易金额
            put("total_fee", "1");
            // 终端 ip
            put("spbill_create_ip", "127.0.0.1");
            // 通知地址
            put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
            // 交易类型 - 扫码支付
            put("trade_type", "NATIVE");
        }};
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        // 设置请求参数
        httpClient.setXmlParam(WXPayUtil.generateSignedXml(requestMap, WxPayInfoUtil.partnerkey));
        httpClient.setHttps(true);
        // 发送 post 请求
        httpClient.post();
        // 获取响应数据
        String content = httpClient.getContent();
        // 将响应数据转换成 Map
        Map<String, String> responseMap = WXPayUtil.xmlToMap(content);
        String responseCode = responseMap.get("result_code");
        HashMap<String, Object> resultMap = new HashMap<String, Object>() {{
            put("orderId", orderId);
            put("totalFee", orderInfo.getAmount());
            put("resultCode", responseCode);
            put("code_url", responseMap.get("code_url"));
        }};
        // 保存到 redis 中并设置两秒有效
        if ("SUCCESS".equalsIgnoreCase(responseCode)) {
            resultStr = JSONObject.toJSONString(resultMap);
            redisTemplate.opsForValue().set(RedisKeyConstant.ORDER_WX_NATIVE + orderId, resultStr, 60 * 60 * 2, TimeUnit.MINUTES);
        }
        return resultMap;
    }

    @Override
    public Map queryPayStatus(String orderId) throws Exception {
        OrderInfo orderInfo = orderService.getById(orderId);
        if (orderInfo == null) {
            throw new BaseServiceException(ResultCodeEnum.PARAM_ERROR);
        }
        // 定义查询参数
        Map requestMap = new HashMap<String, Object>(4) {{
            put("appid", WxPayInfoUtil.appid);
            put("mch_id", WxPayInfoUtil.partner);
            // 订单流水号，这个要和下单时使用的一样
            put("out_trade_no", orderInfo.getOutTradeNo());
            put("nonce_str", WXPayUtil.generateNonceStr());
        }};

        //2、设置请求
        HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        client.setXmlParam(WXPayUtil.generateSignedXml(requestMap, WxPayInfoUtil.partnerkey));
        client.setHttps(true);
        client.post();

        //3. 获取响应 xml 字符串。转换为 map 方便操作
        return WXPayUtil.xmlToMap(client.getContent());
    }

    @Override
    public boolean refund(String orderId) throws Exception {
        PaymentInfo paymentInfo = paymentInfoService.getByOrderId(orderId, PaymentTypeEnum.WEIXIN.getStatus());
        RefundInfo refundInfo = refundService.saveRefundInfo(paymentInfo);
        if (refundInfo.getRefundStatus().intValue() == RefundStatusEnum.REFUND.getStatus()) {
            return true;
        }
        Map<String, String> requestMap = new HashMap<String, String>(8){{
            this.put("appid", WxPayInfoUtil.appid);       //公众账号ID
            this.put("mch_id", WxPayInfoUtil.partner);   //商户编号
            this.put("nonce_str", WXPayUtil.generateNonceStr());
            this.put("transaction_id", paymentInfo.getTradeNo()); //微信订单号
            this.put("out_trade_no", paymentInfo.getOutTradeNo()); //商户订单编号
            this.put("out_refund_no", "tk" + paymentInfo.getOutTradeNo()); //商户退款单号
            this.put("total_fee", "1");
            this.put("refund_fee", "1");
        }};
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
        httpClient.setXmlParam(WXPayUtil.generateSignedXml(requestMap, WxPayInfoUtil.partnerkey));
        httpClient.setHttps(true);
        httpClient.setCert(true);
        httpClient.setCertPassword(WxPayInfoUtil.partner);
        httpClient.post();

        Map<String, String> responseMap = WXPayUtil.xmlToMap(httpClient.getContent());
        if ("SUCCESS".equalsIgnoreCase(responseMap.get("result_code"))) {
            // 更新退款记录
            refundInfo.setTradeNo(responseMap.get("refund_id"));
            refundInfo.setCallbackTime(new Date());
            refundInfo.setCallbackContent(JSONObject.toJSONString(responseMap));
            refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
            refundService.updateById(refundInfo);
            return true;
        }
        return false;
    }

}
