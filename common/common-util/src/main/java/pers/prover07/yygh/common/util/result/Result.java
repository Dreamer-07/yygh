package pers.prover07.yygh.common.util.result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Classname Result
 * @Description 定义统一封装的结果集
 * @Date 2021/11/18 19:27
 * @Created by Prover07
 */
@Data
@Api(tags = "统一封装结果集")
public class Result<T> {

    @ApiModelProperty("状态码")
    private Integer code;

    @ApiModelProperty("相关信息")
    private String message;

    @ApiModelProperty("数据")
    private T data;

    /**
     * 快速构建 result -> for data
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> build(T data) {
        Result<T> result = new Result<>();
        if (data != null) {
            result.setData(data);
        }
        return result;
    }

    /**
     * 快读构建 result -> data & resultCodeEnum[code, message]
     * @param data
     * @param resultCodeEnum
     * @param <T>
     * @return
     */
    public static <T> Result<T> build(T data, ResultCodeEnum resultCodeEnum) {
        Result<T> result = build(data);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }


    /**
     * 快速构建 result -> 自定义 code & message
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    public static <T> Result<T> build(Integer code, String message) {
        Result<T> result = build(null);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 操作成功 - 返回数据
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> ok(T data) {
        return build(data, ResultCodeEnum.SUCCESS);
    }

    /**
     * 操作成功 - 不返回数据
     * @param <T>
     * @return
     */
    public static <T> Result<T> ok() {
        return Result.ok(null);
    }

    /**
     * 操作失败 - 返回数据
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> fail(T data) {
        return build(data, ResultCodeEnum.FAIL);
    }

    /**
     * 操作失败 - 不返回数据
     * @param <T>
     * @return
     */
    public static <T> Result<T> fail() {
        return fail(null);
    }

    /**
     * 设置 message 属性
     * @param message
     * @return
     */
    public Result<T> message(String message) {
        this.setMessage(message);
        return this;
    }

    /**
     * 设置 code 属性
     * @param code
     * @return
     */
    public Result<T> code(Integer code) {
        this.setCode(code);
        return this;
    }

    /**
     * 判断当前 result 响应是否成功
     * @return
     */
    public boolean isOk() {
        return this.code.intValue() == ResultCodeEnum.SUCCESS.getCode();
    }

}
