package pers.prover07.yygh.vo.hosp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @Classname DepartmentQueryVo
 * @Description Department 检索条件实体类
 * @Date 2021/11/25 15:38
 * @Created by Prover07
 */
@ApiModel("Department 检索条件实体类")
@Builder
@Data
public class DepartmentQueryVo {

    @ApiModelProperty(value = "医院编号")
    private String hoscode;

    @ApiModelProperty(value = "科室编号")
    private String depcode;

    @ApiModelProperty(value = "科室名称")
    private String depname;

    @ApiModelProperty(value = "大科室编号")
    private String bigcode;

    @ApiModelProperty(value = "大科室名称")
    private String bigname;

}
