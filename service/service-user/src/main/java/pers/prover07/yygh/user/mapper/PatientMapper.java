package pers.prover07.yygh.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.prover07.yygh.model.user.Patient;

/**
 * @Classname PatientMapper
 * @Description TODO
 * @Date 2021/12/6 10:21
 * @Created by Prover07
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}
