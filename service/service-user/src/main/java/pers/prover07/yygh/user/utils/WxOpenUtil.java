package pers.prover07.yygh.user.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Classname WxOpenUtil
 * @Description TODO
 * @Date 2021/12/2 14:33
 * @Created by Prover07
 */
@Component
@ConfigurationProperties(prefix = "wx.open")
public class WxOpenUtil {

    public static String appId;

    public static String appSecret;

    public static String redirectUrl;

    public static String baseUrl;

    public void setAppId(String appId) {
        WxOpenUtil.appId = appId;
    }

    public void setAppSecret(String appSecret) {
        WxOpenUtil.appSecret = appSecret;
    }

    public void setRedirectUrl(String redirectUrl) {
        WxOpenUtil.redirectUrl = redirectUrl;
    }

    public void setBaseUrl(String baseUrl) {
        WxOpenUtil.baseUrl = baseUrl;
    }
}
