package pers.prover07.yygh.common.util.exception;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pers.prover07.yygh.common.util.result.ResultCodeEnum;

import java.util.function.Supplier;

/**
 * @Classname BaseServiceException
 * @Description 业务异常类
 * @Date 2021/11/19 10:55
 * @Created by Prover07
 */
@ApiModel("业务异常类")
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class BaseServiceException extends RuntimeException{

    @ApiModelProperty("异常状态码")
    private int code;

    public BaseServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BaseServiceException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }



}
