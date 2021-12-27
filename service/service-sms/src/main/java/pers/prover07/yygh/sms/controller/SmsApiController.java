package pers.prover07.yygh.sms.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.sms.service.SmsService;

/**
 * @Classname SmsApiController
 * @Description TODO
 * @Date 2021/12/1 19:59
 * @author  by Prover07
 */
@RestController
@RequestMapping("/api/sms")
public class SmsApiController {

    @Autowired
    private SmsService smsService;

    @GetMapping("/send/{phone}")
    public Result<Object> sendCodeToPhone(@PathVariable String phone) {
        // 判断手机号
        if (StringUtils.isBlank(phone)) {
            return Result.fail().message("手机号错误");
        }
        boolean isSuccess = smsService.send(phone);
        return isSuccess ? Result.ok() : Result.fail().message("发送验证码错误");
    }

}
