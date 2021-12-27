package pers.prover07.yygh.sms.util;

import ch.qos.logback.classic.gaffer.AppenderDelegate;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author by Prover07
 * @Classname YunTongXunSmsUtil
 * @Description 使用联容云通讯技术发送验证码
 * @Date 2021/12/1 19:37
 */
@Component
@ConfigurationProperties(prefix = "yuntongxun.sms")
@Data
public class YunTongXunSmsUtil {

    public static String serverIp;

    public static String serverPort;

    public static String accountSid;

    public static String authToken;

    public static String appId;

    public void setServerIp(String serverIp) {
        YunTongXunSmsUtil.serverIp = serverIp;
    }

    public void setServerPort(String serverPort) {
        YunTongXunSmsUtil.serverPort = serverPort;
    }

    public void setAccountSid(String accountSid) {
        YunTongXunSmsUtil.accountSid = accountSid;
    }

    public void setAuthToken(String authToken) {
        YunTongXunSmsUtil.authToken = authToken;
    }

    public void setAppId(String appId) {
        YunTongXunSmsUtil.appId = appId;
    }
}
