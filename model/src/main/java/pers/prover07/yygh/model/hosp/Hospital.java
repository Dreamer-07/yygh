package pers.prover07.yygh.model.hosp;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pers.prover07.yygh.model.base.BaseEntity;

/**
 * @Classname Hospital
 * @Description 医院实体类
 * @Date 2021/11/24 19:40
 * @Created by Prover07
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document("hospital")
@ApiModel(value = "MongoDB 医院文档实体类")
public class Hospital extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("唯一标识")
    @Id
    private String id;

    @ApiModelProperty("医院编号")
    private String hoscode;

    @ApiModelProperty(value = "医院名称")
    private String hosname;

    @ApiModelProperty(value = "医院类型")
    private String hostype;

    @ApiModelProperty(value = "省code")
    private String provinceCode;

    @ApiModelProperty(value = "市code")
    private String cityCode;

    @ApiModelProperty(value = "区code")
    private String districtCode;

    @ApiModelProperty(value = "详情地址")
    private String address;

    @ApiModelProperty(value = "医院logo")
    private String logoData;

    @ApiModelProperty(value = "医院简介")
    private String intro;

    @ApiModelProperty(value = "坐车路线")
    private String route;

    @ApiModelProperty(value = "状态 0：未上线 1：已上线")
    private Integer status;

    //预约规则
    @ApiModelProperty(value = "预约规则")
    private BookingRule bookingRule;

    public void setBookingRule(String bookingRule) {
        this.bookingRule = JSONObject.parseObject(bookingRule, BookingRule.class);
    }


}
