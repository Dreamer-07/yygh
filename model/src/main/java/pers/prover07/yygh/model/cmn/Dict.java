package pers.prover07.yygh.model.cmn;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.prover07.yygh.model.base.BaseEntity;

/**
 * @Classname Dict
 * @Description TODO
 * @Date 2021/11/22 16:51
 * @Created by Prover07
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("数据字典数据对象")
@TableName("dict")
public class Dict extends BaseEntity {

    private static final long  serialVersionUID = 1L;

    @ApiModelProperty("数据标识")
    @TableId(type = IdType.INPUT)
    private String id;

    @ApiModelProperty("上级标识")
    @TableField("parent_id")
    private String parentId;


    @ApiModelProperty("名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("值")
    @TableField("value")
    private String value;

    @ApiModelProperty("编码")
    @TableField("dict_code")
    private String dictCode;

    @ApiModelProperty("是否含有子节点")
    @TableField(exist = false)
    private Boolean hasChildren;

}
