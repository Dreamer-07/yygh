package pers.prover07.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.prover07.yygh.enums.DictEnum;
import pers.prover07.yygh.model.user.Patient;
import pers.prover07.yygh.user.feign.CmnDictFeignClient;
import pers.prover07.yygh.user.mapper.PatientMapper;
import pers.prover07.yygh.user.service.PatientService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname PatientServiceImpl
 * @Description TODO
 * @Date 2021/12/6 10:22
 * @Created by Prover07
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private CmnDictFeignClient cmnDictFeignClient;

    @Override
    public List<Patient> listByUserId(String userId) {
        List<Patient> patients = this.list(new QueryWrapper<Patient>().eq("user_id", userId));
        patients.forEach(this::packInfo);
        return patients;
    }

    @Override
    public Patient getInfoById(String id) {
        Patient patient = this.getById(id);
        this.packInfo(patient);
        return patient;
    }

    /**
     * '包装' 下 Patient 中的信息
     * @param patient
     */
    private void packInfo(Patient patient) {
        // 获取就诊人证件类型字符串
        String certificatesTypeStr = cmnDictFeignClient.getDictName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());
        // 获取就诊人关联的联系人证件类型字符串
        String contactsCertificatesTypeStr = cmnDictFeignClient.getDictName(DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getContactsCertificatesType());

        // 获取省市区
        String provinceStr = cmnDictFeignClient.getDictName(patient.getProvinceCode());
        String cityStr = cmnDictFeignClient.getDictName(patient.getCityCode());
        String districtStr = cmnDictFeignClient.getDictName(patient.getDistrictCode());

        Map<String, Object> params = patient.getParams();
        params.putAll(new HashMap<String, String>(){{
            put("certificatesTypeString", certificatesTypeStr);
            put("contactsCertificatesTypeString", contactsCertificatesTypeStr);
            put("provinceString", provinceStr);
            put("cityString", cityStr);
            put("districtString", districtStr);
            put("fullAddress", provinceStr + "-" + cityStr + "-" + districtStr + patient.getAddress());
        }});
    }
}
