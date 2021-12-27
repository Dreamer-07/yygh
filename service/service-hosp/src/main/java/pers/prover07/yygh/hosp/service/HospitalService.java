package pers.prover07.yygh.hosp.service;

import org.springframework.data.domain.Page;
import pers.prover07.yygh.model.hosp.Hospital;
import pers.prover07.yygh.vo.hosp.HospitalQueryVo;
import pers.prover07.yygh.vo.order.SignInfoVo;

import java.util.List;
import java.util.Map;

/**
 * @Classname HospitalServic
 * @Description TODO
 * @Date 2021/11/24 19:48
 * @Created by Prover07
 */
public interface HospitalService {
    void save(Map<String, Object> dataMap);

    Hospital getHospitalByHoscode(String hoscode);

    /**
     * 获取医院分页数据
     * @param page
     * @param limit
     * @param hospitalQueryVo
     * @return
     */
    Page<Hospital> getHospitalPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    /**
     * 更新医院上线状态
     * @param id
     * @param state
     */
    void updateStatus(String id, Integer state);

    /**
     * 根据 id 查询出 HospitalDetails
     * @param id
     * @return
     */
    Hospital getHospById(String id);

    /**
     * 根据 hoscode 获取 hosname
     * @param hoscode
     * @return
     */
    String getHosnameByHoscode(String hoscode);

    /**
     * 根据 hospname 查询医院信息
     * @param hosname
     * @return
     */
    List<Hospital> listByHosname(String hosname);

    /**
     * 获取医院预约信息
     * @param hoscode
     * @return
     */
    Map<String, Object> getHospBookingRule(String hoscode);

}
