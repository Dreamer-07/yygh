package pers.prover07.yygh.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Classname WrapperPick
 * @Description TODO
 * @Date 2021/12/6 19:37
 * @Created by Prover07
 */
@Getter
@AllArgsConstructor
public enum WrapperPick {

    /**
     * AND 连接
     */
    AND(false),

    /**
     * OR 连接
     */
    OR(true);

    private final boolean flag;

}
