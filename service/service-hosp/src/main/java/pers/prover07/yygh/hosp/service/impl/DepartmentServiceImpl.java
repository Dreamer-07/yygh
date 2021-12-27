package pers.prover07.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Service;
import pers.prover07.yygh.common.util.exception.BaseServiceException;
import pers.prover07.yygh.common.util.result.ResultCodeEnum;
import pers.prover07.yygh.hosp.repository.DepartmentRepository;
import pers.prover07.yygh.hosp.service.DepartmentService;
import pers.prover07.yygh.model.hosp.Department;
import pers.prover07.yygh.vo.hosp.DepartmentQueryVo;
import pers.prover07.yygh.vo.hosp.DepartmentVo;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Classname DepartmentServiceImpl
 * @Description TODO
 * @Date 2021/11/25 14:08
 * @Created by Prover07
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndex(){
        mongoTemplate.indexOps(Department.class).ensureIndex(new Index("hoscode", Sort.Direction.DESC));
        mongoTemplate.indexOps(Department.class).ensureIndex(new Index("depcode", Sort.Direction.DESC).unique());
    }

    @Override
    public void save(Map<String, Object> dataMap) {
        // 转换成数据实体类
        Department department = JSON.parseObject(JSON.toJSONString(dataMap), Department.class);

        // 判断 mongodb 中是否存在
        Department existsDepartment = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());

        if (existsDepartment != null) {
            // 不为空，表示修改
            department.setCreateTime(existsDepartment.getCreateTime());
        } else {
            // 为空，表示添加
            department.setCreateTime(new Date());
        }
        department.setUpdateTime(new Date());
        department.setIsDeleted(0);
        departmentRepository.save(department);
    }

    @Override
    public Page<Department> getPageDepartment(Integer page, Integer pageSize, DepartmentQueryVo queryVo) {
        // 构建分页数据对象
        Pageable pageable = PageRequest.of(page, pageSize);
        // 构建模糊条件检索数据对象
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                // 模糊匹配
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                // 忽略大小写
                .withIgnoreCase(true);
        // 构建 WHERE 条件对象
        Department queryDepartment = new Department();
        BeanUtils.copyProperties(queryVo, queryDepartment);
        Example<Department> example = Example.of(queryDepartment, exampleMatcher);

        // 查询
        return departmentRepository.findAll(example, pageable);
    }

    @Override
    public void removeDepartment(String hoscode, String depcode) {
        // 查询出对应的科室信息
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            departmentRepository.deleteById(department.getId());
            return;
        }
        throw new BaseServiceException(ResultCodeEnum.FAIL);
    }

    @Override
    public List<DepartmentVo> getTreeDepartmentByHoscode(String hoscode) {
        // 获取 hoscode 对应的所有科室信息
        List<Department> allDepartment = departmentRepository.findAll(Example.of(Department.builder().hoscode(hoscode).build()));
        // 根据 bigcode 进行分组
        Map<String, List<Department>> groupDepartment = allDepartment.stream().collect(Collectors.groupingBy(Department::getBigcode));

        ArrayList<DepartmentVo> result = new ArrayList<>();

        // 遍历分组数据
        for (Map.Entry<String, List<Department>> entry : groupDepartment.entrySet()) {
            String bigcode = entry.getKey();
            List<Department> departmentList = entry.getValue();

            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(bigcode);
            departmentVo.setDepname(departmentList.get(0).getBigname());

            // 封装子数据
            List<DepartmentVo> childVos = departmentList.stream().map(department -> {
                DepartmentVo childVo = new DepartmentVo();
//                childVo.setDepcode(department.getDepcode());
//                childVo.setDepname(department.getDepname());
                BeanUtils.copyProperties(department, childVo);
                return childVo;
            }).collect(Collectors.toList());

            departmentVo.setChildren(childVos);
            result.add(departmentVo);
        }
        return result;
    }

    @Override
    public Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode) {
        return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }

}
