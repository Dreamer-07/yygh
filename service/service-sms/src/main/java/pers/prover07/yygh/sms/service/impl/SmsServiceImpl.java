package pers.prover07.yygh.sms.service.impl;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pers.prover07.yygh.common.util.RandomUtil;
import pers.prover07.yygh.service.util.constrant.RedisKeyConstant;
import pers.prover07.yygh.sms.service.SmsService;
import pers.prover07.yygh.sms.util.YunTongXunSmsUtil;
import pers.prover07.yygh.vo.msm.MsmVo;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * {@code @DependsOn("beanname") - 在指定的 bean 加载后再进行加载}
 * @Classname SmsServiceImpl
 * @Description TODO
 * @Date 2021/12/1 20:01
 * @Created by Prover07
 */
@DependsOn("yunTongXunSmsUtil")
@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private CCPRestSmsSDK sdk = new CCPRestSmsSDK();

    @PostConstruct
    public void initSmsSdk() {
        sdk.init(YunTongXunSmsUtil.serverIp, YunTongXunSmsUtil.serverPort);
        sdk.setAccount(YunTongXunSmsUtil.accountSid, YunTongXunSmsUtil.authToken);
        sdk.setAppId(YunTongXunSmsUtil.appId);
        sdk.setBodyType(BodyType.Type_JSON);
    }

    @Override
    public boolean send(String phone) {
        // 先查询 redis 判断是否存在
        String code = (String) redisTemplate.opsForValue().get(RedisKeyConstant.SMS_PHONE_CODE + phone);
        if (!StringUtils.isBlank(code)) {
            return true;
        }
        // 生成要发送的验证码
        String sendCode = RandomUtil.getSixBitRandom();
        // 测试账号模板id固定为1
        String templateId = "1";
        // 发送短信验证码
        HashMap<String, Object> resultMap = sdk.sendTemplateSMS(phone, templateId, new String[]{sendCode, "5"});
        // 如果发送成功(statusCode=00000)就直接返回 true
        if ("000000".equals(resultMap.get("statusCode"))) {
            // 保存到 redis 中并设置过期时间
            redisTemplate.opsForValue().set(RedisKeyConstant.SMS_PHONE_CODE + phone, sendCode,
                                                RedisKeyConstant.SMS_PHONE_CODE_EXPIRED, TimeUnit.MINUTES);
            return true;
        } else {
            log.error("sms - smsService - 发送短信验证码失败: {状态码: {}, 错误信息: {}}",
                    resultMap.get("statusCode"), resultMap.get("statusMsg"));
            return false;
        }
    }

    @Override
    public boolean sendForAmqp(MsmVo msmVo) {
        String phone = msmVo.getPhone();
        if (!StringUtils.isBlank(phone)) {
            return this.send(phone);
        }
        return false;
    }
}
