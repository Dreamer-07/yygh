package pers.prover07.yygh.vo.hosp;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname HospitalSetQueryVo
 * @Description 查询 医院设置数据 需要的 Vo 类
 * @Date 2021/11/18 20:20
 * @Created by Prover07
 */
@Data
public class HospitalSetQueryVo {

    @ApiModelProperty("医院名")
    private String hosname;

    @ApiModelProperty("医院标识")
    private String hoscode;

}
