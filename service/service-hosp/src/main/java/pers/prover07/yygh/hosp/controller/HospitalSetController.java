package pers.prover07.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.prover07.yygh.common.util.MD5;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.hosp.service.HospitalSetService;
import pers.prover07.yygh.model.hosp.HospitalSet;
import pers.prover07.yygh.vo.hosp.HospitalSetQueryVo;

import java.util.List;
import java.util.Random;

/**
 * @Classname HospitalController
 * @Description HospitalSet 对应的 web 接口
 * @Date 2021/11/18 15:04
 * @Created by Prover07
 */
@Api(tags = "管理医院设置信息")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    /**
     * 查询所有医院设置信息
     *
     * @return
     */
    @ApiOperation("获取所有医院设置信息")
    @GetMapping("/findAll")
    private Result<List<HospitalSet>> findAllHospitalSet() {
        return Result.ok(hospitalSetService.list());
    }

    /**
     * 分页查询 医院设置信息
     *
     * @param currentPage        请求页码
     * @param limit              页数
     * @param hospitalSetQueryVo 封装检索条件 Vo 类
     * @return
     */
    @ApiOperation("分页查询 医院设置信息")
    @PostMapping("/findPage/{currentPage}/{limit}")
    private Result<Page<HospitalSet>> findPageHospitalSet(@ApiParam(value = "当前页数", required = true) @PathVariable("currentPage") Long currentPage,
                                                          @ApiParam(value = "每页显示条数", required = true) @PathVariable("limit") Long limit,
                                                          @ApiParam(value = "检索数据的封装对象") @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        // 1. 构建 Page 对象
        Page<HospitalSet> page = new Page<>(currentPage, limit);

        // 2. 构建检索条件
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isBlank(hosname)) {
            queryWrapper.like("hosname", hosname);
        }
        if (!StringUtils.isBlank(hoscode)) {
            queryWrapper.eq("hoscode", hoscode);
        }

        // 3. 检索数据
        return Result.ok(hospitalSetService.page(page, queryWrapper));
    }

    /**
     * 逻辑删除指定 id 的 HospitalSet
     *
     * @param id 唯一标识
     * @return
     */
    @ApiOperation("逻辑删除医院信息")
    @ApiParam(name = "id", value = "对应的数据标识")
    @DeleteMapping("{id}")
    private Result<Boolean> removeHospitalSetById(@PathVariable String id) {
        boolean isSuc = hospitalSetService.removeById(id);
        // 返回对应的业务处理情况
        if (isSuc) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    /**
     * 保存 医院设置 信息
     *
     * @param hospitalSet 数据体
     * @return
     */
    @ApiOperation("保存医院设置信息")
    @PostMapping("/saveHospitalSet")
    public Result<HospitalSet> saveHospitalSet(@ApiParam(value = "医院设置数据体", required = true) @RequestBody HospitalSet hospitalSet) {
        // 设置状态为启动
        hospitalSet.setStatus(1);
        // 设置签名
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + new Random().nextInt(1000)));
        // 保存到数据库中
        boolean isSuc = hospitalSetService.save(hospitalSet);
        if (isSuc) {
            return Result.ok(hospitalSet);
        } else {
            return Result.fail();
        }
    }

    /**
     * 根据 ID 获取医院设置信息
     *
     * @param id
     * @return
     */
    @ApiOperation("根据 ID 获取医院设置信息")
    @GetMapping("/getHospSet/{id}")
    public Result<HospitalSet> getHospitalSetById(@ApiParam(value = "数据标识", required = true) @PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    /**
     * 修改医院设置数据
     *
     * @param hospitalSet
     * @return
     */
    @ApiOperation("修改医院设置数据")
    @PutMapping("/updateHospSet")
    public Result<Object> updateHospitalSetById(@ApiParam(value = "修改数据的封装对象", required = true) @RequestBody HospitalSet hospitalSet) {
        boolean isSuc = hospitalSetService.updateById(hospitalSet);
        if (isSuc) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    /**
     * 根据 id 批量删除多个数据
     *
     * @param ids
     * @return
     */
    @ApiOperation("根据 id 批量删除多个数据")
    @DeleteMapping("/batchRemove")
    public Result<Object> batchRemoveByIds(@ApiParam(value = "删除的 id 集合", required = true) @RequestBody List<Long> ids) {
        hospitalSetService.removeByIds(ids);
        return Result.ok();
    }

    /**
     * 修改对应的医院设置信息是否处于锁定状态
     *
     * @param id
     * @param status
     * @return
     */
    @ApiOperation("是否锁定医院设置信息")
    @PutMapping("/isLock/{id}/{status}")
    public Result<Object> lockHospitalSetStatus(@ApiParam(value = "数据唯一标识", required = true) @PathVariable String id,
                                                @ApiParam(value = "是否锁定(1-解锁，0-锁定)", required = true) @PathVariable Integer status) {
        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setId(id);
        hospitalSet.setStatus(status);
        boolean isSuc = hospitalSetService.updateById(hospitalSet);
        if (isSuc) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    /**
     * 根据数据标识发送 Hospital 中的 signKey & hoscode 到对应联系人的手机中去
     *
     * @param id
     * @return
     */
    @ApiOperation("获取 SignKey")
    @GetMapping("/sendSignKey/{id}")
    public Result<Object> sendSignKey(@ApiParam(value = "唯一标识", required = true) @PathVariable String id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String hoscode = hospitalSet.getHoscode();
        String signKey = hospitalSet.getSignKey();
        // TODO: 发送短信验证码
        return Result.ok();
    }
}

