package pers.prover07.yygh.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @Classname AuthStatusEnum
 * @Description TODO
 * @Date 2021/12/5 19:50
 * @Created by Prover07
 */
@AllArgsConstructor
@Getter
public enum AuthStatusEnum {

    /**
     * 未认证
     */
    NO_AUTH(0, "未认证"),
    /**
     * 认证中
     */
    AUTH_RUN(1, "认证中"),
    /**
     * 认证成功
     */
    AUTH_SUCCESS(2, "认证成功"),
    /**
     * 认证失败
     */
    AUTH_FAIL(-1, "认证失败"),
    ;

    private Integer status;
    private String name;

    /**
     * 根据 status 对应的数值获取对应的 status name
     * @param status
     * @return
     */
    public static String getStatusNameByStatus(Integer status){
        // 获取所有 enum 对象
        AuthStatusEnum[] enums = AuthStatusEnum.values();
        for (AuthStatusEnum statusEnum : enums) {
            if (status.intValue() == statusEnum.status.intValue()){
                return statusEnum.name;
            }
        }
        return "";
    }

}
