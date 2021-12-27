package pers.prover07.yygh.anno;

import pers.prover07.yygh.enums.WrapperPick;
import pers.prover07.yygh.enums.WrapperScheme;

import java.lang.annotation.*;

/**
 * @Classname WrapperField
 * @Description TODO
 * @Date 2021/12/6 19:34
 * @author  by Prover07
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WrapperField {

    /**
     * 操作的字段
     * @return
     */
    String[] name() default "";

    /**
     * 选择类型(AND/OR)
     * @return
     */
    WrapperPick type() default WrapperPick.AND;

    /**
     * 查询方法
     * @return
     */
    WrapperScheme[] scheme() default WrapperScheme.EQ;

}
