package pers.prover07.yygh.order.service.impl;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.prover07.yygh.common.util.exception.BaseServiceException;
import pers.prover07.yygh.common.util.helper.HttpRequestHelper;
import pers.prover07.yygh.common.util.result.ResultCodeEnum;
import pers.prover07.yygh.enums.OrderStatusEnum;
import pers.prover07.yygh.enums.PaymentStatusEnum;
import pers.prover07.yygh.model.order.OrderInfo;
import pers.prover07.yygh.model.order.PaymentInfo;
import pers.prover07.yygh.order.feign.HospFeignClient;
import pers.prover07.yygh.order.mapper.PaymentInfoMapper;
import pers.prover07.yygh.order.service.OrderService;
import pers.prover07.yygh.order.service.PaymentInfoService;
import pers.prover07.yygh.vo.order.SignInfoVo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author by Prover07
 * @classname PaymentInfoServiceImpl
 * @description TODO
 * @date 2021/12/13 10:58
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HospFeignClient hospFeignClient;

    @Override
    public void addPaymentInfo(OrderInfo orderInfo, Integer paymentType) {
        // 判断订单是否已经存在
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderInfo.getId());
        queryWrapper.eq("payment_type", paymentType);
        int count = this.count(queryWrapper);
        if (count > 0) {
            return;
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setUpdateTime(new Date());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        String subject = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + "|" + orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(orderInfo.getAmount());
        this.save(paymentInfo);
    }

    @Override
    public void paySuccessCallback(String outTradeNo, Integer paymentType, Map<String, String> resultMap) {
        // 更新交易记录
        PaymentInfo paymentInfo = this.getOne(new QueryWrapper<PaymentInfo>()
                .eq("out_trade_no", outTradeNo)
                .eq("payment_type", paymentType)
        );
        if (paymentInfo == null || paymentInfo.getPaymentStatus() != PaymentStatusEnum.UNPAID.getStatus().intValue()) {
            throw new BaseServiceException(ResultCodeEnum.PARAM_ERROR);
        }
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfo.setTradeNo(resultMap.get("transaction_id"));
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(resultMap.toString());
        this.updateById(paymentInfo);

        // 更新订单信息
        OrderInfo orderInfo = orderService.getById(paymentInfo.getOrderId());
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderService.updateById(orderInfo);

        // 调用医院接口，通知订单状态更新
        SignInfoVo signInfoVo = hospFeignClient.getHospSignInfo(orderInfo.getHoscode());
        HashMap requestMap = new HashMap<String, Object>() {{
            put("hoscode", orderInfo.getHoscode());
            put("hosRecordId", orderInfo.getHosRecordId());
            put("timestamp", HttpRequestHelper.getTimestamp());
            put("sign", HttpRequestHelper.getSign(this, signInfoVo.getSignKey()));
        }};
        JSONObject jsonObject = HttpRequestHelper.sendRequest(requestMap, signInfoVo.getApiUrl() + "/order/updatePayStatus");
        if (jsonObject.getInteger("code") != 200) {
            throw new BaseServiceException(ResultCodeEnum.PARAM_ERROR);
        }
    }

    @Override
    public PaymentInfo getByOrderId(String orderId, Integer paymentType) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<PaymentInfo>();
        queryWrapper.eq("order_id", orderId);
        queryWrapper.eq("payment_type", paymentType);
        return this.getOne(queryWrapper);
    }

}
