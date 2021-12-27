package pers.prover07.yygh.service.util;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.StringUtils;
import pers.prover07.yygh.anno.WrapperField;
import pers.prover07.yygh.enums.WrapperScheme;

import java.lang.reflect.Field;

/**
 * @author by Prover07
 * @Classname QueryWrapperUtil
 * @Description 辅助构建 QueryWrapper
 * @Date 2021/12/6 19:44
 */
public class QueryWrapperUtil<T, E> {

    /**
     * 私有化构造器 - 单例模式
     */
    private QueryWrapperUtil() {

    }

    public static QueryWrapperUtil getInstance() {
        return new QueryWrapperUtil();
    }

    private QueryWrapper<T> wrapper;

    private Object qo;

    /**
     * 构建 QueryWrapper
     * @param qo
     * @param wrapper
     * @return
     */
    public QueryWrapper<T> wrapper(Object qo, QueryWrapper<T> wrapper) {
        this.wrapper = wrapper;
        this.qo = qo;

        Class<?> clazz = qo.getClass();
        // 获取所有属性
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 读取私有属性
            field.setAccessible(true);
            // 获取属性值
            E value = this.handle(field);
            // 获取字段名
            String columnName = this.transformColumn(field.getName());
            // 获取 WrapperField 注解
            WrapperField annotation = field.getAnnotation(WrapperField.class);
            if (annotation == null) {
                // 如果没有注解直接用 eq 拼接字段名和属性值即可
                wrapper = wrapper.eq(this.judgementCondition(value), columnName, value);
                continue;
            }
            // 获取注解上配置的数据(字段名&连接方式&操作类型)
            String[] columnNames = annotation.name();
            boolean flag = annotation.type().isFlag();
            WrapperScheme[] schemes = annotation.scheme();
            for (WrapperScheme scheme : schemes) {
                Integer schemeValue = scheme.getValue();

                // 只操作一个字段
                if (columnNames.length == 1 && "".equals(columnNames[0].trim())) {
                    this.wrapper(schemeValue, columnName, value);
                    continue;
                }
                // 一个属性需要操作多个字段
                for (int index = 0; index < columnNames.length; index++) {
                    this.wrapper(schemeValue, columnNames[index], value);
                    this.flag(flag, index, columnNames.length - 1);
                }
            }
        }
        return wrapper;
    }

    /**
     * 拼接 AND / OR 语句 -- 其实这里没有使用 AND , 因为其实 MP 拼接 SQL 条件的时候可以不用手动调用 AND()
     *
     * @param flag  标识位
     * @param index 当前字段的索引(最后一个不用拼接)
     * @param len   查询字段的所有长度
     */
    private void flag(boolean flag, Integer index, Integer len) {
        // Index -
        boolean indexIf = (!flag || index != null);
        // 当前字段不是最后一个
        boolean lenIf = (len != null && index < len);
        if (indexIf && lenIf) {
            wrapper.or();
        }
    }

    /**
     * 进行指定操作
     *
     * @param schemeValue 操作类型
     * @param columnName  字段名
     * @param value       值
     */
    private void wrapper(Integer schemeValue, String columnName, E value) {
        boolean flag = this.judgementCondition(value);
        // 获取操作类型对应的枚举对象
        WrapperScheme scheme = WrapperScheme.getWrapperSchemeByValue(schemeValue);
        switch (scheme) {
            case GT:
                wrapper.gt(flag, columnName, value);
                break;
            case GE:
                wrapper.ge(flag, columnName, value);
                break;
            case LT:
                wrapper.lt(flag, columnName, value);
                break;
            case LE:
                wrapper.le(flag, columnName, value);
                break;
            case LIKE:
                wrapper.like(flag, columnName, value);
                break;
            case ORDER_ASC:
                wrapper.orderByAsc(flag, columnName);
                break;
            case ORDER_DESC:
                wrapper.orderByDesc(flag, columnName);
                break;
            case NULL:
                if (!flag) {
                    break;
                }
                // 当值为 true 表示 IS NOT NULL 判断
                if (Boolean.parseBoolean(value.toString())) {
                    wrapper.isNotNull(columnName);
                    break;
                }
                // 当值为 false 表示 IS NULL 判断
                wrapper.isNull(columnName);
                break;
            default:
                wrapper.eq(flag, columnName, value);
                break;
        }
    }

    /**
     * 判断数据是否正确(为 null 什么的)
     *
     * @param value
     * @return
     */
    private boolean judgementCondition(E value) {
        return value != null && StringUtils.hasLength(value.toString());
    }


    /**
     * 驼峰数据 -> 下划线数据
     * 原理:
     * StringBuffer sb = new StringBuffer();
     * sb.append(ele);
     * for (int i = 0; i < ele.length(); i++) {
     * char c = sb.charAt(i);
     * if (c < 97) {
     * c = (char) (c + 32);
     * sb.delete(i, i + 1);
     * sb.insert(i, "_" + c);
     * }
     * }
     * return sb.toString();
     *
     * @param fieldName
     * @return
     */
    private String transformColumn(String fieldName) {
        return StrUtil.toUnderlineCase(fieldName);
    }

    /**
     * 获取属性值
     *
     * @param field
     * @return
     */
    private E handle(Field field) {
        try {
            return (E) field.get(qo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
