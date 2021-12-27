package pers.prover07.yygh.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import pers.prover07.yygh.anno.WrapperField;
import pers.prover07.yygh.enums.WrapperScheme;

@Data
@ApiModel(description="会员搜索对象")
public class UserInfoQueryVo {

    @ApiModelProperty(value = "关键字")
    @WrapperField(name = "name", scheme = WrapperScheme.LIKE)
    private String keyword;

    @ApiModelProperty(value = "状态")
    @WrapperField
    private Integer status;

    @ApiModelProperty(value = "认证状态")
    @WrapperField
    private Integer authStatus;

    @ApiModelProperty(value = "创建时间")
    @WrapperField(name = "create_time", scheme = WrapperScheme.GE)
    private String createTimeBegin;

    @ApiModelProperty(value = "创建时间")
    @WrapperField(name = "create_time", scheme = WrapperScheme.LE)
    private String createTimeEnd;

}