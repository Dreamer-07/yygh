package pers.prover07.yygh.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import pers.prover07.yygh.anno.WrapperField;
import pers.prover07.yygh.enums.WrapperScheme;

import java.util.Date;

@Data
@ApiModel(description = "Order")
public class OrderQueryVo {


	@ApiModelProperty(value = "会员id")
	@WrapperField
	private String userId;
	
	@ApiModelProperty(value = "订单交易号")
	private String outTradeNo;

	@ApiModelProperty(value = "就诊人id")
	private Long patientId;

	@ApiModelProperty(value = "就诊人")
	private String patientName;

	@ApiModelProperty(value = "医院名称")
	@WrapperField
	private String keyword;

	@ApiModelProperty(value = "订单状态")
	@WrapperField
	private String orderStatus;

	@ApiModelProperty(value = "安排日期")
	@WrapperField(scheme = WrapperScheme.GE)
	private String reserveDate;

	@ApiModelProperty(value = "创建时间")
	@WrapperField(name = "create_time", scheme = WrapperScheme.GE)
	private String createTimeBegin;

	@WrapperField(name = "create_time", scheme = WrapperScheme.LE)
	private String createTimeEnd;

}

