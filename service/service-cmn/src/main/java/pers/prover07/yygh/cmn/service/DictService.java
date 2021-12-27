package pers.prover07.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import pers.prover07.yygh.model.cmn.Dict;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Classname DictService
 * @Description Dict 数据字段对应的业务规范接口
 * @Date 2021/11/22 19:19
 * @Created by Prover07
 */
public interface DictService extends IService<Dict> {

    /**
     * 根据上级标识(parentId)查找出对应的子数据
     * @param parentId
     * @return
     */
    List<Dict> findChildByParentId(String parentId);

    /**
     * 导出数据到 Excel 表格
     * @param response
     */
    void exportDataToExcel(HttpServletResponse response);

    /**
     * 从 Excel 中导入数据
     * @param multipartFile
     */
    void importDataForExcel(MultipartFile multipartFile);

    /**
     * 根据 DictCode & value 获取 DictName
     * @param dictCode
     * @param value
     * @return
     */
    String getDictName(String dictCode, String value);

    /**
     * 根据 dictCode 获取相关所有子节点数据
     * @param dictCode
     * @return
     */
    List<Dict> getByDictCode(String dictCode);
}
