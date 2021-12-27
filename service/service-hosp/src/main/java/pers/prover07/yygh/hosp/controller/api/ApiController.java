package pers.prover07.yygh.hosp.controller.api;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.prover07.yygh.common.util.MD5;
import pers.prover07.yygh.common.util.exception.BaseServiceException;
import pers.prover07.yygh.common.util.helper.HttpRequestHelper;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.common.util.result.ResultCodeEnum;
import pers.prover07.yygh.hosp.service.DepartmentService;
import pers.prover07.yygh.hosp.service.HospitalService;
import pers.prover07.yygh.hosp.service.HospitalSetService;
import pers.prover07.yygh.hosp.service.ScheduleService;
import pers.prover07.yygh.model.hosp.Department;
import pers.prover07.yygh.model.hosp.Hospital;
import pers.prover07.yygh.model.hosp.HospitalSet;
import pers.prover07.yygh.model.hosp.Schedule;
import pers.prover07.yygh.vo.hosp.DepartmentQueryVo;
import pers.prover07.yygh.vo.hosp.ScheduleQueryVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Classname HospitalController
 * @Description TODO
 * @Date 2021/11/24 19:51
 * @Created by Prover07
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation("保存医院信息")
    @PostMapping("/saveHospital")
    public Result<Object> saveHospital(HttpServletRequest request){
        // 获取所有请求参数
        Map<String, Object> dataMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // -- 校验 signKey
        // 获取医院系统传递过来的医院编号，获取医院系统传递过来的签名(该签名已经经过 MD5 加密)
        this.checkSignKey((String) dataMap.get("hoscode"), (String) dataMap.get("sign"));

        // -- Base64 图片字符串处理
        // 将图片经过 Base64 加密后再进行传输时，会将 "+" -> " " 所以要转回来
        String logoData = (String) dataMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        dataMap.put("logoData", logoData);

        // -- 保存数据
        hospitalService.save(dataMap);

        // -- 返回结果
        return Result.ok();
    }

    @ApiOperation("查询医院信息")
    @PostMapping("/hospital/show")
    public Result<Hospital> getHospital(HttpServletRequest request){
        // 获取所有请求参数
        Map<String, Object> dataMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // -- 校验 signKey
        // 获取医院系统传递过来的医院编号，获取医院系统传递过来的签名(该签名已经经过 MD5 加密)
        this.checkSignKey((String) dataMap.get("hoscode"), (String) dataMap.get("sign"));

        // 通过 hoscode 获取医院接口信息
        Hospital hospital = hospitalService.getHospitalByHoscode((String) dataMap.get("hoscode"));

        return Result.ok(hospital);
    }

    @ApiOperation("保存科室信息")
    @PostMapping("/saveDepartment")
    public Result<Object> saveDepartment(HttpServletRequest request){
        // 获取数据
        Map<String, Object> dataMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // 检查医院 sign key
        this.checkSignKey((String) dataMap.get("hoscode"), (String) dataMap.get("sign"));

        // 保存信息
        departmentService.save(dataMap);

        return Result.ok();
    }

    @ApiOperation("查询科室接口")
    @PostMapping("/department/list")
    public Result<Page<Department>> getPageDepartment(HttpServletRequest request){
        // 获取数据
        Map<String, Object> dataMap = HttpRequestHelper.switchMap(request.getParameterMap());

        // 校验签名
        this.checkSignKey((String) dataMap.get("hoscode"), (String) dataMap.get("sign"));

        // 获取分页信息
        Integer page = Integer.parseInt((String) ObjectUtil.defaultIfNull(dataMap.get("page"), "1"));
        Integer pageSize = Integer.parseInt((String) ObjectUtil.defaultIfNull(dataMap.get("limit"), "10"));

        // 封装条件检索对象
        DepartmentQueryVo departmentQueryVo = DepartmentQueryVo.builder().hoscode((String) dataMap.get("hoscode")).build();

        // 获取分页数据对象
        Page<Department> departmentPage = departmentService.getPageDepartment(page, pageSize, departmentQueryVo);

        return Result.ok(departmentPage);
    }

    @ApiOperation("删除科室信息")
    @PostMapping("/department/remove")
    public Result<Object> removeDepartment(HttpServletRequest request){
        Map<String, Object> dataMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 校验签名
        this.checkSignKey((String) dataMap.get("hoscode"), (String) dataMap.get("sign"));

        departmentService.removeDepartment((String) dataMap.get("hoscode"), (String) dataMap.get("depcode"));

        return Result.ok();
    }

    @ApiOperation("查询排班信息")
    @PostMapping("/schedule/list")
    public Result<Page<Schedule>> getPageSchedule(HttpServletRequest request){
        Map<String, Object> dataMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 校验签名
        this.checkSignKey((String) dataMap.get("hoscode"), (String) dataMap.get("sign"));

        // 设置检索条件
        ScheduleQueryVo scheduleQueryVo = ScheduleQueryVo.builder().hoscode((String) dataMap.get("hoscode")).build();

        // 获取分页信息
        Integer page = dataMap.get("page") == null ? 1 : Integer.parseInt((String) dataMap.get("page"));
        Integer pageSize = dataMap.get("limit") == null ? 10 : Integer.parseInt((String) dataMap.get("limit"));

        // 查询分页信息
        Page<Schedule> schedulePage = scheduleService.getPageSchedule(page, pageSize, scheduleQueryVo);
        return Result.ok(schedulePage);
    }

    @ApiOperation("上传排班信息")
    @PostMapping("/saveSchedule")
    public Result<Object> uploadScheduleInfo(HttpServletRequest request){
        Map<String, Object> dataMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 校验签名
        this.checkSignKey((String) dataMap.get("hoscode"), (String) dataMap.get("sign"));

        scheduleService.saveSchedule(dataMap);

        return Result.ok();
    }

    @ApiOperation("删除排班信息")
    @PostMapping("/schedule/remove")
    public Result<Object> removeSchedule(HttpServletRequest request){
        Map<String, Object> dataMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 校验签名
        this.checkSignKey((String) dataMap.get("hoscode"), (String) dataMap.get("sign"));

        scheduleService.removeSchedule((String) dataMap.get("hoscode"), (String) dataMap.get("hosScheduleId"));

        return Result.ok();
    }

    /**
     * 检查签名
     * @param hoscode 医院编号(唯一)
     * @param sourceSign 源签名
     * @return
     */
    private void checkSignKey(String hoscode, String sourceSign) {
        // 获取数据库中保存的指定 hoscode 的 sign
        HospitalSet hospitalSet = hospitalSetService.getByHoscode(hoscode);
        // 将 sign 进行 MD5 加密
        String targetSign = MD5.encrypt(hospitalSet.getSignKey());
        // 判断是否相同
        if (!sourceSign.equals(targetSign)){
            // 如果不同就直接抛出异常
            throw new BaseServiceException(ResultCodeEnum.SIGN_ERROR);
        }
    }
}
