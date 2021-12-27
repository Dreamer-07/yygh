package pers.prover07.yygh.order.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author by Prover07
 * @classname WxPayUtil
 * @description TODO
 * @date 2021/12/13 11:22
 */
@Component
@ConfigurationProperties(prefix = "weixin.pay")
@Getter
public class WxPayInfoUtil {

    public static String appid;

    public static String partner;

    public static String partnerkey;

    public static String cert;

    public void setAppid(String appid) {
        WxPayInfoUtil.appid = appid;
    }

    public void setPartner(String partner) {
        WxPayInfoUtil.partner = partner;
    }

    public void setPartnerkey(String partnerkey) {
        WxPayInfoUtil.partnerkey = partnerkey;
    }

    public void setCert(String cect) {
        WxPayInfoUtil.cert = cect;
    }
}
