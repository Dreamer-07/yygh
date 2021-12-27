package pers.prover07.yygh.oss.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pers.prover07.yygh.oss.config.OssConfig;
import pers.prover07.yygh.oss.service.FileApiService;

import java.io.IOException;

/**
 * @Classname FileApiService
 * @Description TODO
 * @Date 2021/12/5 16:08
 * @Created by Prover07
 */
@Service
public class FileApiServiceImpl implements FileApiService {

    @Autowired
    private OSS oss;
    @Autowired
    private OssConfig ossConfig;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Override
    public String upload(MultipartFile file) throws IOException {
        // 生成唯一标识
        String uuid = IdUtil.fastSimpleUUID();
        String originalFilename = file.getOriginalFilename();

        // 以时间为文件夹进行目录管理
        String today = DateUtil.today();

        String filename = today + "/" + uuid + originalFilename;

        // 发送请求上传到 OSS
        oss.putObject(bucketName, filename, file.getInputStream());

        // 返回文件名
        return String.format("https://%s.%s/%s", ossConfig.getBucketName(), ossConfig.getEndpoint(), filename);
    }
}
