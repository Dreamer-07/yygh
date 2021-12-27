package pers.prover07.yygh.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.prover07.yygh.model.hosp.HospitalSet;
import pers.prover07.yygh.vo.order.SignInfoVo;

/**
 * @Classname HospitalSetService
 * @Description HospitalSet 实体类对应的业务规范
 * @Date 2021/11/18 14:59
 * @Created by Prover07
 */
public interface HospitalSetService extends IService<HospitalSet> {

    /**
     * 根据 hscode 查询医院设置信息
     * @param hoscode
     * @return
     */
    HospitalSet getByHoscode(String hoscode);

    /**
     * 获取签名信息
     * @param hoscode
     * @return
     */
    SignInfoVo getSignInfo(String hoscode);
}
