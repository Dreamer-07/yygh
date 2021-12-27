package pers.prover07.yygh.hosp.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.hosp.feign.CmnDictFeignClient;
import pers.prover07.yygh.hosp.service.HospitalService;
import pers.prover07.yygh.model.hosp.Hospital;
import pers.prover07.yygh.vo.hosp.HospitalQueryVo;

import java.util.HashMap;

/**
 * @Classname HospitalController
 * @Description TODO
 * @Date 2021/11/26 9:00
 * @Created by Prover07
 */
@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @PostMapping("/list/{page}/{limit}")
    public Result<Page<Hospital>> getPageHospital(@PathVariable Integer page,
                                                  @PathVariable Integer limit,
                                                  @RequestBody(required = false) HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitalPage = hospitalService.getHospitalPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitalPage);
    }

    @ApiOperation("更新医院是否上线")
    @PutMapping("/updateHospStatus/{id}/{state}")
    public Result<Object> updateHospStatus(@PathVariable String id, @PathVariable Integer state){
        hospitalService.updateStatus(id, state);
        return Result.ok();
    }

    @ApiOperation("查看医院详情")
    @GetMapping("/showHospDetails/{id}")
    public Result<Hospital> showHospDetails(@PathVariable String id){
        Hospital hospital = hospitalService.getHospById(id);
        return Result.ok(hospital);
    }

}
