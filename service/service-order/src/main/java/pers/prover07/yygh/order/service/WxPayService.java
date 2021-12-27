package pers.prover07.yygh.order.service;

import java.util.Map;

/**
 * @author by Prover07
 * @classname WxPayService
 * @description TODO
 * @date 2021/12/13 11:05
 */
public interface WxPayService {

    /**
     * 获取微信支付的二维码
     * @param orderId
     * @return
     */
    Map createNative(String orderId) throws Exception;

    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    Map queryPayStatus(String orderId) throws Exception;

    /**
     * 退款接口
     * @param orderId
     * @return
     */
    boolean refund(String orderId) throws Exception;
}
