package pers.prover07.yygh.hosp.controller.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.hosp.service.DepartmentService;
import pers.prover07.yygh.hosp.service.HospitalService;
import pers.prover07.yygh.model.hosp.Hospital;
import pers.prover07.yygh.vo.hosp.DepartmentVo;
import pers.prover07.yygh.vo.hosp.HospitalQueryVo;

import java.util.List;
import java.util.Map;

/**
 * @Classname HospitalApiController
 * @Description TODO
 * @Date 2021/11/30 19:23
 * @Created by Prover07
 */
@RestController
@RequestMapping("/api/hosp/hospital")
public class HospitalApiController {

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/list/{page}/{limit}")
    @ApiOperation("查询医院信息")
    public Result<Page<Hospital>> getPageHospital(@PathVariable Integer page,
                                       @PathVariable Integer limit,
                                       @RequestBody(required = false) HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitalPage = hospitalService.getHospitalPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitalPage);
    }

    @GetMapping("/search")
    @ApiOperation("根据名字搜索医院")
    public Result<List<Hospital>> searchListByHospname(@RequestParam("hosname") String hosname){
        List<Hospital> hospitals = hospitalService.listByHosname(hosname);
        return Result.ok(hospitals);
    }

    @GetMapping("/getHospDepList/{hoscode}")
    @ApiOperation("根据 hoscode 获取对应的科室列表")
    public Result<List<DepartmentVo>> getDepartmentByHoscode(@PathVariable String hoscode){
        List<DepartmentVo> departmentVos = departmentService.getTreeDepartmentByHoscode(hoscode);
        return Result.ok(departmentVos);
    }

    @GetMapping("/getHospBookingRule/{hoscode}")
    @ApiOperation("根据 hosocde 获取对应的预约挂号信息")
    public Result<Map<String, Object>> getHospBookingRule(@PathVariable String hoscode){
        Map<String, Object> dataMap = hospitalService.getHospBookingRule(hoscode);
        return Result.ok(dataMap);
    }

}
