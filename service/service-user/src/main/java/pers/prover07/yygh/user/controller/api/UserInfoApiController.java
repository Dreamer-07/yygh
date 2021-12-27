package pers.prover07.yygh.user.controller.api;

import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.prover07.yygh.common.util.JwtUtil;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.enums.AuthStatusEnum;
import pers.prover07.yygh.model.user.UserInfo;
import pers.prover07.yygh.user.service.UserInfoService;
import pers.prover07.yygh.vo.user.LoginVo;
import pers.prover07.yygh.vo.user.UserAuthVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Classname UserInfoControoler
 * @Description TODO
 * @Date 2021/12/1 14:17
 * @author  by Prover07
 */
@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {

    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation("手机验证码登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> loginOfPhone(@RequestBody LoginVo loginVo){
        Map<String, Object> dataMap = userInfoService.login(loginVo);
        return Result.ok(dataMap);
    }

    @ApiOperation("用户信息认证接口")
    @PostMapping("/auth")
    public Result<Object> authUser(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request){
        // 获取 token 中的用户西悉尼
        String token = request.getHeader("token");
        String userId = JwtUtil.getTokenInfo(token, "userId", String.class);

        userInfoService.authUser(userId, userAuthVo);
        return Result.ok();
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public Result<UserInfo> getUserInfoById(HttpServletRequest request) {
        // 获取 token 中的用户信息
        String token = request.getHeader("token");
        String userId = JwtUtil.getTokenInfo(token, "userId", String.class);

        UserInfo userInfo = userInfoService.getById(userId);

        userInfo.getParams().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));

        return Result.ok(userInfo);
    }
}
