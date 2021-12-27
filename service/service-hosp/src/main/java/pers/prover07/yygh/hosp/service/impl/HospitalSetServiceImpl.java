package pers.prover07.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.prover07.yygh.common.util.exception.BaseServiceException;
import pers.prover07.yygh.common.util.result.ResultCodeEnum;
import pers.prover07.yygh.hosp.mapper.HospitalSetMapper;
import pers.prover07.yygh.hosp.service.HospitalSetService;
import pers.prover07.yygh.model.hosp.HospitalSet;
import pers.prover07.yygh.vo.order.SignInfoVo;

/**
 * @Classname HospitalSetSericeImpl
 * @Description HospitalSet 实体类对应的业务逻辑
 * @Date 2021/11/18 15:01
 * @Created by Prover07
 */
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {
    @Override
    public HospitalSet getByHoscode(String hoscode) {
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hoscode", hoscode);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public SignInfoVo getSignInfo(String hoscode) {
        HospitalSet hospitalSet = this.getByHoscode(hoscode);
        if (hospitalSet == null) {
            throw new BaseServiceException(ResultCodeEnum.PARAM_ERROR);
        }
        SignInfoVo signInfoVo = new SignInfoVo();
        signInfoVo.setApiUrl(hospitalSet.getApiUrl());
        signInfoVo.setSignKey(hospitalSet.getSignKey());
        return signInfoVo;
    }
}
