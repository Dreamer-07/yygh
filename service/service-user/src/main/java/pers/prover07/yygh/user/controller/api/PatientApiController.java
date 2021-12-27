package pers.prover07.yygh.user.controller.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.prover07.yygh.common.util.JwtUtil;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.model.user.Patient;
import pers.prover07.yygh.user.service.PatientService;

import javax.xml.xpath.XPath;
import java.util.List;

/**
 * @Classname PatientController
 * @Description TODO
 * @Date 2021/12/6 10:23
 * @Created by Prover07
 */
@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController {

    @Autowired
    private PatientService patientService;

    /**
     * 根据 userid 获取相关联的就诊人
     * @param token
     * @return
     */
    @ApiOperation("获取关联的就诊人列表")
    @GetMapping("/list")
    public Result<List<Patient>> listByUserId(@RequestHeader("token") String token) {
        String userId = JwtUtil.getTokenInfo(token, "userId", String.class);

        List<Patient> patients = patientService.listByUserId(userId);

        return Result.ok(patients);
    }

    @ApiOperation("获取指定的就诊人信息")
    @GetMapping("/info/{id}")
    public Result<Patient> getInfoById(@PathVariable String id){
        Patient patient = patientService.getInfoById(id);

        return Result.ok(patient);
    }

    @ApiOperation("添加就诊人信息")
    @PostMapping("/add")
    public Result<Object> addPatientInfo(@RequestBody Patient patient, @RequestHeader("token") String token){
        // 获取用户标识
        String userId = JwtUtil.getTokenInfo(token, "userId", String.class);
        patient.setUserId(userId);

        patientService.save(patient);

        return Result.ok();
    }

    @ApiOperation("修改就诊人信息")
    @PutMapping("/update")
    public Result<Object> updatePatientInfo(@RequestBody Patient patient){
        patientService.updateById(patient);
        return Result.ok();
    }

    @ApiOperation("删除就诊人信息")
    @DeleteMapping("/del/{id}")
    public Result<Object> delInfoById(@PathVariable String id){
        patientService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("远程调用 - 获取就诊人信息")
    @GetMapping("/inner/info/{id}")
    public Patient getInfoByIdInner(@PathVariable("id") String patientId){
        return patientService.getInfoById(patientId);
    }

}
