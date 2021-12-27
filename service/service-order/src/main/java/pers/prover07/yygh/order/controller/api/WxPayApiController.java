package pers.prover07.yygh.order.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.enums.PaymentTypeEnum;
import pers.prover07.yygh.order.service.PaymentInfoService;
import pers.prover07.yygh.order.service.WxPayService;
import pers.prover07.yygh.order.service.impl.PaymentInfoServiceImpl;
import rx.internal.util.unsafe.MpmcArrayQueue;

import java.util.Map;

/**
 * @author by Prover07
 * @classname WxPayApiController
 * @description TODO
 * @date 2021/12/13 11:01
 */
@RestController
@RequestMapping("/api/order/wx")
public class WxPayApiController {

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private PaymentInfoService paymentInfoService;

    @ApiOperation("获取订单的微信支付二维码")
    @GetMapping("/createNative/{orderId}")
    public Result<Map> createNative(@PathVariable String orderId) throws Exception {
        Map dataMap = wxPayService.createNative(orderId);
        return Result.ok(dataMap);
    }

    @ApiOperation("查询支付状态")
    @GetMapping("/queryPayStatus/{orderId}")
    public Result queryPayStatus(@PathVariable String orderId) throws Exception {
        Map resultMap = wxPayService.queryPayStatus(orderId);
        if (resultMap == null) {
            return Result.fail().message("支付出错啦!");
        } else if ("SUCCESS".equals(resultMap.get("trade_state"))) {
            String outTradeNo = (String) resultMap.get("out_trade_no");
            paymentInfoService.paySuccessCallback(outTradeNo, PaymentTypeEnum.WEIXIN.getStatus(), resultMap);
            return Result.ok().message("支付成功");
        }
        return Result.ok().message("支付中");
    }

}
