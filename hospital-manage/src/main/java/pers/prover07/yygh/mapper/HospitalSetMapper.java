package pers.prover07.yygh.mapper;

import pers.prover07.yygh.model.HospitalSet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HospitalSetMapper extends BaseMapper<HospitalSet> {

}
