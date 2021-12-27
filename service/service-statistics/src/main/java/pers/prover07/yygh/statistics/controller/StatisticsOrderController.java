package pers.prover07.yygh.statistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.statistics.feign.OrderFeignClient;
import pers.prover07.yygh.vo.order.OrderCountQueryVo;

import java.util.List;
import java.util.Map;

/**
 * @author by Prover07
 * @classname StatisticsOrderController
 * @description TODO
 * @date 2021/12/14 11:07
 */
@RestController
@RequestMapping("/admin/statistics/order")
public class StatisticsOrderController {

    @Autowired
    private OrderFeignClient orderFeignClient;

    @PostMapping("/countOrderInfo")
    public Result<Map> countOrderInfo(@RequestBody OrderCountQueryVo orderCountQueryVo){
        Map<String, List> dataMap = orderFeignClient.countOrderInfo(orderCountQueryVo);
        return Result.ok(dataMap);
    }

}
