package pers.prover07.yygh.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.model.user.UserInfo;
import pers.prover07.yygh.user.service.UserInfoService;
import pers.prover07.yygh.vo.user.UserInfoQueryVo;

import java.util.Map;

/**
 * @Classname UserInfoController
 * @Description TODO
 * @Date 2021/12/6 16:07
 * @Created by Prover07
 */
@RestController
@RequestMapping("/admin/user")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/list/{page}/{limit}")
    public Result<IPage<UserInfo>> pageList(@PathVariable Integer page,
                                            @PathVariable Integer limit,
                                            @RequestBody(required = false) UserInfoQueryVo userInfoQueryVo) {
        IPage<UserInfo> userInfoListPage = userInfoService.selectPage(new Page<>(page, limit), userInfoQueryVo);

        return Result.ok(userInfoListPage);
    }

    @PutMapping("/lock/{userId}/{status}")
    public Result<Object> lock(@PathVariable String userId,
                               @PathVariable Integer status) {
        userInfoService.lockUser(userId, status);
        return Result.ok();
    }

    @ApiOperation("获取用户详情信息")
    @GetMapping("/info/{userId}")
    public Result<Map<String, Object>> getUserDetailInfo(@PathVariable String userId) {
        Map<String, Object> dataMap = userInfoService.getDetailInfo(userId);
        return Result.ok(dataMap);
    }

    @ApiOperation("审核用户认证信息")
    @PutMapping("/approval/auth/{userId}/{authStatus}")
    public Result<Object> approvalUserAuthInfo(@PathVariable String userId, @PathVariable Integer authStatus) {
        userInfoService.approval(userId, authStatus);
        return Result.ok();
    }
}
