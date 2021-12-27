package pers.prover07.yygh.order.feign;

import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pers.prover07.yygh.vo.hosp.ScheduleOrderVo;
import pers.prover07.yygh.vo.order.SignInfoVo;

/**
 * @author by Prover07
 * @classname HospFeignClient
 * @description TODO
 * @date 2021/12/12 23:03
 */
@FeignClient("service-hosp")
@Service
public interface HospFeignClient {

    @GetMapping("/api/hosp/hospSet/inner/getSignInfo/{hoscode}")
    SignInfoVo getHospSignInfo(@PathVariable("hoscode") String hoscode);

    @GetMapping("/api/hosp/schedule/inner/getScheduleInfo/{scheduleId}")
    public ScheduleOrderVo getScheduleDetailInfoInner(@PathVariable("scheduleId") String scheduleId);

}
