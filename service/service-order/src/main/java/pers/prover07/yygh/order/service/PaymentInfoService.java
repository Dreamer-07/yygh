package pers.prover07.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.prover07.yygh.enums.PaymentTypeEnum;
import pers.prover07.yygh.model.order.OrderInfo;
import pers.prover07.yygh.model.order.PaymentInfo;

import java.util.Map;

/**
 * @author by Prover07
 * @classname PaymentInfoService
 * @description TODO
 * @date 2021/12/13 10:58
 */
public interface PaymentInfoService extends IService<PaymentInfo> {
    /**
     * 添加交易记录
     * @param orderInfo 订单信息
     * @param paymentType 交易类型
     */
    void addPaymentInfo(OrderInfo orderInfo, Integer paymentType);

    /**
     * 支付成功后回调 -> 更新交易记录 & 订单信息
     * @param outTradeNo
     * @param status
     * @param resultMap
     */
    void paySuccessCallback(String outTradeNo, Integer status, Map<String, String> resultMap);

    /**
     * 根据 orderId 查询 paymentInfo
     * @param orderId
     * @param paymentType
     * @return
     */
    PaymentInfo getByOrderId(String orderId, Integer paymentType);
}
