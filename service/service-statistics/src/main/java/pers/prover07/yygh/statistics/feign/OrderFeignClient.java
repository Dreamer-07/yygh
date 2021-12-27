package pers.prover07.yygh.statistics.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pers.prover07.yygh.vo.order.OrderCountQueryVo;

import java.util.List;
import java.util.Map;

/**
 * @author by Prover07
 * @classname OrderFeignClient
 * @description TODO
 * @date 2021/12/14 11:05
 */
@FeignClient("service-order")
public interface OrderFeignClient {

    @PostMapping("/api/order/orderInfo/inner/countOrderInfo")
    Map<String, List> countOrderInfo(@RequestBody OrderCountQueryVo orderCountQueryVo);

}
