package pers.prover07.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Classname FileApiService
 * @Description TODO
 * @Date 2021/12/5 16:07
 * @Created by Prover07
 */
public interface FileApiService {
    String upload(MultipartFile file) throws IOException;
}
