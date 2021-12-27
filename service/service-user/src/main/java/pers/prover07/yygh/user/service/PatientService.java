package pers.prover07.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.prover07.yygh.model.user.Patient;

import java.util.List;

/**
 * @Classname PatientService
 * @Description TODO
 * @Date 2021/12/6 10:21
 * @Created by Prover07
 */
public interface PatientService extends IService<Patient> {
    /**
     * 获取 userid 相关的就诊人信息
     * @param userId
     * @return
     */
    List<Patient> listByUserId(String userId);

    /**
     * 根据 id 获取指定的就诊人信息
     * @param id
     * @return
     */
    Patient getInfoById(String id);
}
