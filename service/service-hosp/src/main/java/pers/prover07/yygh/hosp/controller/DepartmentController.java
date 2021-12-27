package pers.prover07.yygh.hosp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.hosp.service.DepartmentService;
import pers.prover07.yygh.vo.hosp.DepartmentVo;

import java.util.List;

/**
 * @Classname DepartmentController
 * @Description TODO
 * @Date 2021/11/29 15:08
 * @Created by Prover07
 */
@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 根据 hoscode(医院编号) 获取对应的所有科室信息
     * @param hoscode
     * @return
     */
    @GetMapping("/tree/{hoscode}")
    public Result<List<DepartmentVo>> getTreeDepartment(@PathVariable String hoscode){
        List<DepartmentVo> departmentVos = departmentService.getTreeDepartmentByHoscode(hoscode);
        return Result.ok(departmentVos);
    }

}
