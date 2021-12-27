package pers.prover07.yygh.common.util.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pers.prover07.yygh.common.util.result.Result;

/**
 * @Classname GlobalExceptionHandler
 * @Description 全局异常处理器
 * @Date 2021/11/19 11:10
 * @Created by Prover07
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseServiceException.class)
    public Result<Object> handleBaseServiceException(BaseServiceException serviceException) {
        serviceException.printStackTrace();
        return Result.build(serviceException.getCode(), serviceException.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception exception) {
        exception.printStackTrace();
        return Result.fail();
    }

}
