package pers.prover07.yygh.model.hosp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import pers.prover07.yygh.model.base.BaseEntity;

/**
 * @Classname HospitalSet
 * @Description 医院设置实体类
 * @Date 2021/11/18 14:44
 * @Created by Prover07
 */
@Data
@ApiModel(description = "医院设置实体类")
@TableName("hospital_set")
public class HospitalSet extends BaseEntity {

    private static final long  serialVersionUID = 1L;

    @ApiModelProperty("唯一标识")
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("医院名")
    @TableField("hosname")
    private String hosname;

    @ApiModelProperty("医院编号")
    @TableField("hoscode")
    private String hoscode;

    @ApiModelProperty("api 基础路径")
    @TableField("api_url")
    private String apiUrl;

    @ApiModelProperty("签名密钥")
    @TableField("sign_key")
    private String signKey;

    @ApiModelProperty("联系人姓名")
    @TableField("contacts_name")
    private String contactsName;

    @ApiModelProperty("联系人手机")
    @TableField("contacts_phone")
    private String contactsPhone;

    @ApiModelProperty("是否可以使用")
    @TableField("status")
    private Integer status;


}
