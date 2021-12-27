package pers.prover07.yygh.order.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.prover07.yygh.common.util.JwtUtil;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.enums.OrderStatusEnum;
import pers.prover07.yygh.model.order.OrderInfo;
import pers.prover07.yygh.order.service.OrderService;
import pers.prover07.yygh.vo.order.OrderCountQueryVo;
import pers.prover07.yygh.vo.order.OrderQueryVo;

import java.util.List;
import java.util.Map;

/**
 * @author by Prover07
 * @classname OrderApiController
 * @description TODO
 * @date 2021/12/9 13:06
 */
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/commit/{patientId}/{scheduleId}")
    public Result<String> saveOrderInfo(@PathVariable String patientId, @PathVariable String scheduleId) {
        String orderId = orderService.saveOrderInfo(patientId, scheduleId);
        return Result.ok(orderId);
    }

    @GetMapping("/show/{orderId}")
    public Result<OrderInfo> getOrderInfoDetail(@PathVariable String orderId) {
        OrderInfo orderInfo = orderService.getDetailById(orderId);
        return Result.ok(orderInfo);
    }

    @PostMapping("/list/{page}/{limit}")
    public Result<IPage<OrderInfo>> getOrderInfoList(@PathVariable Integer page, @PathVariable Integer limit,
                                          OrderQueryVo orderQueryVo, @RequestHeader("token") String token) {
        String userId = JwtUtil.getTokenInfo(token, "userId", String.class);
        orderQueryVo.setUserId(userId);
        IPage<OrderInfo> orderInfoByUser = orderService.getOrderInfoByUser(new Page<OrderInfo>(page, limit), orderQueryVo);
        return Result.ok(orderInfoByUser);
    }

    @ApiOperation(value = "获取订单状态")
    @GetMapping("/getStatusList")
    public Result<Object> getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }

    @ApiOperation("取消订单申请退款")
    @GetMapping("/cancelPay/{orderId}")
    public Result<Boolean> cancelPayment(@PathVariable String orderId) throws Exception {
        boolean isSuccess = orderService.cancelPayment(orderId);
        return Result.ok(isSuccess);
    }

    @ApiOperation("统计订单信息")
    @PostMapping("/inner/countOrderInfo")
    public Map<String, List> countOrderInfo(@RequestBody OrderCountQueryVo orderCountQueryVo) {
        return orderService.countOrderInfo(orderCountQueryVo);
    }
}
