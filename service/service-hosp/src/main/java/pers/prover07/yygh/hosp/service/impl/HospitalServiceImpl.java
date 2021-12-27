package pers.prover07.yygh.hosp.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Service;
import pers.prover07.yygh.common.util.exception.BaseServiceException;
import pers.prover07.yygh.common.util.result.ResultCodeEnum;
import pers.prover07.yygh.hosp.feign.CmnDictFeignClient;
import pers.prover07.yygh.hosp.repository.HospitalRepository;
import pers.prover07.yygh.hosp.service.HospitalService;
import pers.prover07.yygh.model.hosp.Hospital;
import pers.prover07.yygh.vo.hosp.HospitalQueryVo;
import pers.prover07.yygh.vo.order.SignInfoVo;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname HospitalServiceImpl
 * @Description TODO
 * @Date 2021/11/24 19:49
 * @Created by Prover07
 */
@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CmnDictFeignClient cmnDictFeignClient;

    @PostConstruct
    public void initIndex() {
        // 创建唯一索引
        mongoTemplate.indexOps(Hospital.class).ensureIndex(new Index("hoscode", Sort.Direction.DESC).unique());
        // 创建索引
        mongoTemplate.indexOps(Hospital.class).ensureIndex(new Index("hosname", Sort.Direction.DESC));
    }

    @Override
    public void save(Map<String, Object> dataMap) {
        // 将 dataMap 转换成对应的数据实体类
        Hospital hospital = JSON.parseObject(JSON.toJSONString(dataMap), Hospital.class);

        // 判断数据是否存在
        Hospital existHospital = hospitalRepository.getHospitalByHoscode(hospital.getHoscode());

        // 判断是添加还是修改
        if (existHospital != null) {
            // 修改
            hospital.setStatus(existHospital.getStatus());
            hospital.setCreateTime(existHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
        } else {
            // 新增
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
        }
        hospital.setIsDeleted(0);
        hospitalRepository.save(hospital);
    }

    @Override
    public Hospital getHospitalByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }

    @Override
    public Page<Hospital> getHospitalPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        // 构建分页对象
        Pageable pageable = PageRequest.of(page - 1, limit);

        // 构建模糊查询匹配
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Hospital hospital = new Hospital();
        if (!ObjectUtil.isNull(hospitalQueryVo)){
            BeanUtils.copyProperties(hospitalQueryVo, hospital);
        }
        Example<Hospital> example = Example.of(hospital, exampleMatcher);

        Page<Hospital> hospitalPage = hospitalRepository.findAll(example, pageable);
        hospitalPage.getContent().forEach(this::setHostTypeString);
        // 查询
        return hospitalPage;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        // 获取 mongodb 中的原有数据
        Hospital hospital = hospitalRepository.findById(id).orElseThrow(() -> new BaseServiceException(ResultCodeEnum.FAIL));
        // 更新数据
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    @Override
    public Hospital getHospById(String id) {
        Hospital hospital = hospitalRepository.findById(id).orElseThrow(() -> new BaseServiceException(ResultCodeEnum.FAIL));
        this.setHostTypeString(hospital);
        return hospital;
    }

    @Override
    public String getHosnameByHoscode(String hoscode) {
        Hospital hospital = this.getHospitalByHoscode(hoscode);
        if (hospital == null) {
            throw new BaseServiceException(ResultCodeEnum.FAIL);
        }
        return hospital.getHosname();
    }

    @Override
    public List<Hospital> listByHosname(String hosname) {
        return hospitalRepository.getAllByHosnameLike(hosname);
    }

    @Override
    public Map<String, Object> getHospBookingRule(String hoscode) {
        // 获取 hospital
        Hospital hospital = getHospitalByHoscode(hoscode);
        this.setHostTypeString(hospital);
        Dict dict = Dict.create()
                .set("hospital", hospital)
                .set("bookingRule", hospital.getBookingRule());
        hospital.setBookingRule(null);
        return dict;
    }

    /**
     * 设置 HostType(医院类型)
     * @param hospital
     */
    private void setHostTypeString(Hospital hospital) {
        // 获取 hosType 文字描述
        String hostypeStr = cmnDictFeignClient.getDictName("Hostype", hospital.getHostype());
        // 获取省市区
        // 省
        String province = cmnDictFeignClient.getDictName(hospital.getProvinceCode());
        // 市
        String city = cmnDictFeignClient.getDictName(hospital.getCityCode());
        // 区
        String district = cmnDictFeignClient.getDictName(hospital.getDistrictCode());

        // 保存到 hospital 中
        hospital.getParams().putAll(new HashMap<String, Object>(2){{
            put("hostypeString", hostypeStr);
            put("fullAddress", String.format("%s-%s-%s", province, city, district));
        }});
    }
}
