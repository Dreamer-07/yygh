package pers.prover07.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import pers.prover07.yygh.model.cmn.Dict;
import pers.prover07.yygh.vo.cmn.DictExcelVo;

/**
 * @Classname DictExelReadListener
 * @Description 从 Excel 中导入数据时，通过监听器可以监听导入的数据并存储到 DAO 中
 * @Date 2021/11/23 10:19
 * @Created by Prover07
 */
@NoArgsConstructor
@AllArgsConstructor
public class DictExelReadListener extends AnalysisEventListener<DictExcelVo> {

    private BaseMapper<Dict> baseMapper;

    /**
     * 每读取一行数据都会调用该函数，从第二行开始读取(第一行时表头)
     * @param dictExcelVo
     * @param analysisContext
     */
    @Override
    public void invoke(DictExcelVo dictExcelVo, AnalysisContext analysisContext) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictExcelVo, dict);
        baseMapper.insert(dict);
    }

    /**
     * 读取完所有数据后就会调用该函数
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
