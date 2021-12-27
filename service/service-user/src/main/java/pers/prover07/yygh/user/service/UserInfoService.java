package pers.prover07.yygh.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.prover07.yygh.model.user.UserInfo;
import pers.prover07.yygh.vo.user.LoginVo;
import pers.prover07.yygh.vo.user.UserAuthVo;
import pers.prover07.yygh.vo.user.UserInfoQueryVo;

import java.util.Map;

/**
 * @Classname UserInfoService
 * @Description TODO
 * @Date 2021/12/1 14:15
 * @Created by Prover07
 */
public interface UserInfoService extends IService<UserInfo> {
    /**
     * 手机号登录
     * @param loginVo
     * @return
     */
    Map<String, Object> login(LoginVo loginVo);

    /**
     * 根据 openid 获取用户信息
     * @param openid
     * @return
     */
    UserInfo getByOpenid(String openid);

    /**
     * 保存用户认证信息
     * @param userId
     * @param userAuthVo
     */
    void authUser(String userId, UserAuthVo userAuthVo);

    /**
     * 分页条件查询
     * @param userInfoPage
     * @param userInfoQueryVo
     * @return
     */
    IPage<UserInfo> selectPage(Page<UserInfo> userInfoPage, UserInfoQueryVo userInfoQueryVo);

    /**
     * 锁定/解锁用户
     * @param userId
     * @param status
     */
    void lockUser(String userId, Integer status);

    /**
     * 获取用户详细信息
     * @param userId
     * @return
     */
    Map<String, Object> getDetailInfo(String userId);

    /**
     * 审核用户认证信息
     * @param userId
     * @param authStatus 2(通过认证)/-1(认证不通过)
     */
    void approval(String userId, Integer authStatus);
}
