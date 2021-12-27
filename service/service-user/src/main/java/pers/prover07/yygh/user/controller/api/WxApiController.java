package pers.prover07.yygh.user.controller.api;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pers.prover07.yygh.common.util.HttpClientUtil;
import pers.prover07.yygh.common.util.JwtUtil;
import pers.prover07.yygh.common.util.exception.BaseServiceException;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.common.util.result.ResultCodeEnum;
import pers.prover07.yygh.model.user.UserInfo;
import pers.prover07.yygh.user.service.UserInfoService;
import pers.prover07.yygh.user.utils.WxOpenUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author by Prover07
 * @Classname WxLoginController
 * @Description TODO
 * @Date 2021/12/2 14:44
 */
@Controller
@RequestMapping("/api/ucenter/wx")
@Slf4j
public class WxApiController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 请求 access_token 路径
     */
    private static final StringBuilder REQ_ACCESS_TOKEN_URL = new StringBuilder("https://api.weixin.qq.com/sns/oauth2/access_token?");
    /**
     * 请求用户信息路径
     */
    private static final StringBuilder REQ_USER_INFO_URL = new StringBuilder("https://api.weixin.qq.com/sns/userinfo?");

    static {
        REQ_ACCESS_TOKEN_URL
                .append("appId=%s").append("&")
                .append("secret=%s").append("&")
                .append("code=%s").append("&")
                .append("grant_type=authorization_code");

        REQ_USER_INFO_URL
                .append("openid=%s").append("&")
                .append("access_token=%s");
    }

    /**
     * 获取微信登录信息
     *
     * @return
     */
    @GetMapping("/login/conf")
    @ResponseBody
    public Result<Map<String, Object>> getWxLoginConf() throws UnsupportedEncodingException {
        HashMap<String, Object> dataMap = new HashMap<String, Object>() {{
            put("appid", WxOpenUtil.appId);
            put("scope", "snsapi_login");
            // 对重定向地址进行 UrlEncode 编码
            put("redirect_uri", URLEncoder.encode(WxOpenUtil.redirectUrl, "UTF-8"));
            put("state", System.currentTimeMillis() + "");
        }};
        return Result.ok(dataMap);
    }

    /**
     * 微信登录回调接口
     */
    @GetMapping("/callback")
    public String wxLoginCallback(@RequestParam String code, @RequestParam String state) throws UnsupportedEncodingException {

        // 获取 accessToken
        JSONObject accessTokenByCode = this.getAccessTokenByCode(code);
        String accessToken = accessTokenByCode.getString("access_token");
        String openid = accessTokenByCode.getString("openid");
        log.info("user-wx-login: {accessToken: {}, openid: {}}", accessToken, openid);

        // 根据 openid 查询数据库，判断用户是否已经注册过
        UserInfo userInfo = userInfoService.getByOpenid(openid);

        // 未注册过 -- 注册 userinfo
        if (userInfo == null) {
            // 获取用户信息
            JSONObject userInfoByAccessToken = this.getUserInfoByAccessToken(openid, accessToken);
            log.info("user-wx-login: {userinfo: {}}", userInfoByAccessToken);

            userInfo = new UserInfo();
            userInfo.setNickName(userInfoByAccessToken.getString("nickname"));
            userInfo.setName(userInfoByAccessToken.getString("nickname"));
            userInfo.setStatus(1);
            userInfo.setOpenid(openid);
            userInfoService.save(userInfo);
        }
        // 账户被锁定
        if (userInfo.getStatus() == 0) {
            // TODO - 返回异常信息
            return "redirect:" + WxOpenUtil.baseUrl + "/wx/callback?"
                    + "code=" + ResultCodeEnum.LOGIN_DISABLED_ERROR.getCode()
                    + "$errmsg=" + ResultCodeEnum.LOGIN_DISABLED_ERROR.getMessage();
        }
        /*
         * 判断是否注册过, 如果用户是第一次登录，那么需要将 openid 返回给前端
         * 当用户绑定手机号时可以绑定到指定 openid 的账户上
         * */
        HashMap<String, Object> dataMap = new HashMap<String, Object>(3) {{
            put("code", ResultCodeEnum.SUCCESS.getCode());
        }};

        if (StringUtils.isBlank(userInfo.getPhone())) {
            dataMap.put("openid", openid);
        } else {
            String token = JwtUtil.createJwtToken(userInfo.getId(), userInfo.getName());
            String name = URLEncoder.encode(userInfo.getName(), "UTF-8");
            dataMap.put("token", token);
            dataMap.put("name", name);
        }

        return "redirect:" + dataMap.entrySet().stream()
                .reduce(new StringBuilder(WxOpenUtil.baseUrl + "/wx/callback?"), (e1, e2) -> {
                    e1.append(e2.getKey()).append("=").append(e2.getValue()).append("&");
                    return e1;
                }, (a, b) -> null).toString();
    }

    /**
     * 根据 code 获取 access_token
     *
     * @param code
     * @return
     */
    private JSONObject getAccessTokenByCode(String code) {
        // 填充数据
        String requestToken = String.format(REQ_ACCESS_TOKEN_URL.toString(), WxOpenUtil.appId, WxOpenUtil.appSecret, code);

        // 发送 GET 请求获取数据
        String result = null;
        try {
            result = HttpClientUtil.get(requestToken);
        } catch (Exception e) {
            throw new BaseServiceException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        // 转换成 JSONObject 对象
        return JSONObject.parseObject(result);
    }

    /**
     * 根据 access_token & openid 获取 userinfo
     *
     * @param openid
     * @param accessToken
     * @return
     */
    private JSONObject getUserInfoByAccessToken(String openid, String accessToken) {
        // 请求微信用户信息数据
        String requestUserInfo = String.format(REQ_USER_INFO_URL.toString(), openid, accessToken);
        String resultUserInfo = null;
        try {
            resultUserInfo = HttpClientUtil.get(requestUserInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONObject.parseObject(resultUserInfo);
    }

}
