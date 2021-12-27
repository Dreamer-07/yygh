package pers.prover07.yygh.hosp.controller.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.prover07.yygh.hosp.service.HospitalSetService;
import pers.prover07.yygh.vo.order.SignInfoVo;

/**
 * @author by Prover07
 * @classname HospitalSetApiController
 * @description TODO
 * @date 2021/12/9 23:10
 */
@RestController
@RequestMapping("/api/hosp/hospSet")
public class HospitalSetApiController {

    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation("获取医院签名信息")
    @GetMapping("/inner/getSignInfo/{hoscode}")
    public SignInfoVo getHospSignInfo(@PathVariable("hoscode") String hoscode) {
        return hospitalSetService.getSignInfo(hoscode);
    }

}
