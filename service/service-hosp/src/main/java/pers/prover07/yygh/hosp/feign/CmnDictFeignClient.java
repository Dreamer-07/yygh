package pers.prover07.yygh.hosp.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pers.prover07.yygh.common.util.result.Result;

/**
 * @Classname CmnDictFeignClient
 * @Description cmn-dict 模块的 feign client
 * @Date 2021/11/26 9:41
 * @Created by Prover07
 */
@FeignClient("service-cmn")
public interface CmnDictFeignClient {

    @GetMapping("/admin/cmn/dict/getName/{dictCode}/{value}")
    public String getDictName(@PathVariable("dictCode") String dictCode,
                                      @PathVariable("value") String value);

    @GetMapping("/admin/cmn/dict/getName/{value}")
    public String getDictName(@PathVariable("value") String value);

}
