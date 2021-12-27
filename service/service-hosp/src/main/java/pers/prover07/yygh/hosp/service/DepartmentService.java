package pers.prover07.yygh.hosp.service;

import org.springframework.data.domain.Page;
import pers.prover07.yygh.model.hosp.Department;
import pers.prover07.yygh.vo.hosp.DepartmentQueryVo;
import pers.prover07.yygh.vo.hosp.DepartmentVo;

import java.util.List;
import java.util.Map;

/**
 * @Classname DepartmentService
 * @Description TODO
 * @Date 2021/11/25 14:07
 * @Created by Prover07
 */
public interface DepartmentService {
    void save(Map<String, Object> dataMap);

    /**
     * 获取分页数据信息
     * @param page
     * @param pageSize
     * @param queryVo
     */
    Page<Department> getPageDepartment(Integer page, Integer pageSize, DepartmentQueryVo queryVo);

    /**
     * 删除科室信息
     * @param hoscode
     * @param depcode
     */
    void removeDepartment(String hoscode, String depcode);

    /**
     * 根据 hoscode 获取科室信息
     * @param hoscode
     * @return
     */
    List<DepartmentVo> getTreeDepartmentByHoscode(String hoscode);

    /**
     * 根据 hoscode & depcode 获取科室信息
     * @param hoscode
     * @param depcode
     */
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
