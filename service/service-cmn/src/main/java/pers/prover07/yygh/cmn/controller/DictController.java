package pers.prover07.yygh.cmn.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.prover07.yygh.cmn.service.DictService;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.model.cmn.Dict;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @Classname DictController
 * @Description Dict(数据字典)数据对应的接口
 * @Date 2021/11/22 19:23
 * @Created by Prover07
 */
@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @ApiOperation("获取指定标识下的所有数据")
    @GetMapping("/findChild/{parentId}")
    public Result<List<Dict>> findChild(@ApiParam(required = true, value = "上级标识") @PathVariable String parentId) {
        List<Dict> dictList = dictService.findChildByParentId(parentId);
        return Result.ok(dictList);
    }

    @ApiOperation("将数据字典中的数据导出为 Excel 表格")
    @GetMapping("/exportExcel")
    public void exportDataToExcel(HttpServletResponse response) throws UnsupportedEncodingException {
        // 初始化请求头信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和 easyexcel 没有关系
        String fileName = URLEncoder.encode("数据字典", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        dictService.exportDataToExcel(response);
    }

    @ApiOperation("导入 Excel 中的数据到数据库中")
    @PostMapping("/importExcel")
    public Result<Object> importDataForExcel(@ApiParam(required = true, value = "excel 文件") MultipartFile file) {
        dictService.importDataForExcel(file);
        return Result.ok();
    }

    @ApiOperation("根据 dictCode 获取所有子节点")
    @GetMapping("/list/dict_code/{dictCode}")
    public Result<List<Dict>> getListByDictCode(@PathVariable String dictCode) {
        List<Dict> dicts = dictService.getByDictCode(dictCode);
        return Result.ok(dicts);
    }


    @GetMapping("/getName/{dictCode}/{value}")
    public String getDictName(@PathVariable String dictCode,
                              @PathVariable String value) {
        return dictService.getDictName(dictCode, value);
    }

    @GetMapping("/getName/{value}")
    public String getDictName(@PathVariable String value) {
        return dictService.getDictName("", value);
    }

}
