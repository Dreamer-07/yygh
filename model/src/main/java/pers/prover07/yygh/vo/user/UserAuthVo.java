package pers.prover07.yygh.vo.user;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname UserAuthVo
 * @Description TODO
 * @Date 2021/12/5 19:40
 * @author  by Prover07
 */
@Data
@ApiModel(description="会员认证对象")
public class UserAuthVo {

    @ApiModelProperty(value = "用户姓名")
    private String name;

    @ApiModelProperty(value = "证件类型")
    private String certificatesType;

    @ApiModelProperty(value = "证件编号")
    private String certificatesNo;

    @ApiModelProperty(value = "证件路径")
    private String certificatesUrl;

}
