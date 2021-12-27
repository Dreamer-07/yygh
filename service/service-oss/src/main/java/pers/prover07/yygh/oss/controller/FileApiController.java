package pers.prover07.yygh.oss.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.oss.service.FileApiService;

import java.io.IOException;

/**
 * @Classname FileApiController
 * @Description TODO
 * @Date 2021/12/5 16:05
 * @Created by Prover07
 */
@RestController
@RequestMapping("/api/oss/file")
public class FileApiController {

    @Autowired
    private FileApiService fileApiService;

    @ApiOperation("上传图片至 OSS 服务器")
    @PostMapping("/upload")
    public Result<String> uploadFileToOss(MultipartFile file) throws IOException {
        String url = fileApiService.upload(file);
        return Result.ok(url);
    }

}
