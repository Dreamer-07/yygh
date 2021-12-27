package pers.prover07.yygh.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @Classname WrapperScheme
 * @Description TODO
 * @Date 2021/12/6 19:39
 * @author  by Prover07
 */
@AllArgsConstructor
@Getter
public enum WrapperScheme {

    /** 全等判断 */
    EQ(0),
    /** 大于判断 */
    GT(1),
    /** 大于等于判断 */
    GE(2),
    /** 小于判断 */
    LT(3),
    /** 小于等于判断 */
    LE(4),
    /** 模糊查询条件 */
    LIKE(5),

    /** 升序排序*/
    ORDER_ASC(6),
    /** 降序排序*/
    ORDER_DESC(7),
    /** 为空判断*/
    NULL(8);

    private final Integer value;

    /**
     * 根据 value 获取对应的操作对象
     * @param value
     * @return
     */
    public static WrapperScheme getWrapperSchemeByValue(Integer value) {
        WrapperScheme[] schemes = WrapperScheme.values();
        for (WrapperScheme scheme : schemes) {
            if (Objects.equals(value, scheme.getValue())) {
                return scheme;
            }
        }
        return EQ;
    }
}
