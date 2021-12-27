package pers.prover07.yygh.vo.hosp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import pers.prover07.yygh.vo.msm.MsmVo;

/**
 * @author by Prover07
 * @classname ScheduleStockVo
 * @description TODO
 * @date 2021/12/12 21:27
 */
@Data
@ApiModel(description = "科室可预约信息实体类")
public class ScheduleStockVo {

    @ApiModelProperty(value = "可预约数")
    private Integer reservedNumber;

    @ApiModelProperty(value = "剩余预约数")
    private Integer availableNumber;

    @ApiModelProperty(value = "排班id")
    private String scheduleId;

    @ApiModelProperty(value = "短信实体")
    private MsmVo msmVo;


}
