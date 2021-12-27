package pers.prover07.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pers.prover07.yygh.cmn.listener.DictExelReadListener;
import pers.prover07.yygh.cmn.mapper.DictMapper;
import pers.prover07.yygh.cmn.service.DictService;
import pers.prover07.yygh.model.cmn.Dict;
import pers.prover07.yygh.vo.cmn.DictExcelVo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname DictServiceImpl
 * @Description Dict 对应的业务具体的实现逻辑
 * @Date 2021/11/22 19:20
 * @Created by Prover07
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Cacheable(value = "dict", keyGenerator = "keyGenerator")
    @Override
    public List<Dict> findChildByParentId(String parentId) {
        List<Dict> dictList = this.list(new QueryWrapper<Dict>().eq("parent_id", parentId));
        return dictList.stream().peek(dict -> {
            // 判断是否存在子数据
            int count = this.count(new QueryWrapper<Dict>().eq("parent_id", dict.getId()));
            dict.setHasChildren(count > 0);
        }).collect(Collectors.toList());
    }

    @Override
    public void exportDataToExcel(HttpServletResponse response) {
        // 查询出所有数据
        List<Dict> dicts = this.list();
        // 转换成指定的 Excel 数据格式
        List<DictExcelVo> dictExcelVos = dicts.stream().map(dict -> {
            DictExcelVo dictExcelVo = new DictExcelVo();
            BeanUtils.copyProperties(dict, dictExcelVo);
            return dictExcelVo;
        }).collect(Collectors.toList());
        // 导出 Excel
        try {
            EasyExcel.write(response.getOutputStream(), DictExcelVo.class).sheet("数据字典").doWrite(dictExcelVos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @CacheEvict(value = "dict", allEntries = true)
    @Override
    public void importDataForExcel(MultipartFile multipartFile) {
        try {
            EasyExcel.read(multipartFile.getInputStream(), DictExcelVo.class, new DictExelReadListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDictName(String dictCode, String value) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("value", value);

        // 如果 dictCode 不为空，需要做额外处理
        if (!StringUtils.isBlank(dictCode)){
            // 不为空 - 根据 dictCode 查询出对应的父级数据字典的标识
            String parentId = this.getOne(new QueryWrapper<Dict>().eq("dict_code", dictCode)).getId();
            // 查询 parentId 对应的子级数据字典
            queryWrapper.eq("parent_id", parentId);
        }

        Dict dict = this.getOne(queryWrapper);
        return dict.getName();
    }

    @Override
    public List<Dict> getByDictCode(String dictCode) {
        // 根据 dictCode 查询出对应的父级数据字典的标识
        String parentId = this.getOne(new QueryWrapper<Dict>().eq("dict_code", dictCode)).getId();
        return this.findChildByParentId(parentId);
    }

}
