package pers.prover07.yygh.model.hosp;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pers.prover07.yygh.model.base.BaseEntity;

/**
 * @Classname Department
 * @Description 医院科室数据实体类
 * @Date 2021/11/25 14:03
 * @Created by Prover07
 */
@ApiModel("医院科室数据信息实体")
@Builder
@Data
@Document("department")
@NoArgsConstructor
@AllArgsConstructor
public class Department extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一标识")
    @Id
    private String id;

    @ApiModelProperty(value = "医院编号")
    private String hoscode;

    @ApiModelProperty(value = "科室编号")
    private String depcode;

    @ApiModelProperty(value = "科室名称")
    private String depname;

    @ApiModelProperty(value = "科室描述")
    private String intro;

    @ApiModelProperty(value = "大科室编号")
    private String bigcode;

    @ApiModelProperty(value = "大科室名称")
    private String bigname;

}
