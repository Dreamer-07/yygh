package pers.prover07.yygh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.prover07.yygh.model.order.OrderInfo;
import pers.prover07.yygh.vo.order.OrderCountQueryVo;
import pers.prover07.yygh.vo.order.OrderCountVo;

import java.util.List;

/**
 * @author by Prover07
 * @classname OrderMapper
 * @description TODO
 * @date 2021/12/9 13:01
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {

    List<OrderCountVo> countByReserveDate(@Param("orderCountQueryVo") OrderCountQueryVo orderCountQueryVo);

}
