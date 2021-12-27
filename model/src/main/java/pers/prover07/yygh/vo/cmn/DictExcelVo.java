package pers.prover07.yygh.vo.cmn;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @Classname DictExcelVo
 * @Description Dict 数据对象导出 Excel 数据的实体类 Vo
 * @Date 2021/11/23 9:35
 * @Created by Prover07
 */
@Data
public class DictExcelVo {

    @ExcelProperty(value = "标识", index = 0)
    private String id;

    @ExcelProperty(value = "父标识", index = 1)
    private String parentId;

    @ExcelProperty(value = "名称", index = 2)
    private String name;

    @ExcelProperty(value = "值", index = 3)
    private String value;

    @ExcelProperty(value = "编码", index = 4)
    private String dictCode;

}
