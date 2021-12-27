package pers.prover07.yygh.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import pers.prover07.yygh.common.util.JwtUtil;
import pers.prover07.yygh.common.util.exception.BaseServiceException;
import pers.prover07.yygh.common.util.result.ResultCodeEnum;
import pers.prover07.yygh.enums.AuthStatusEnum;
import pers.prover07.yygh.model.user.Patient;
import pers.prover07.yygh.model.user.UserInfo;
import pers.prover07.yygh.service.util.QueryWrapperUtil;
import pers.prover07.yygh.service.util.constrant.RedisKeyConstant;
import pers.prover07.yygh.user.mapper.UserInfoMapper;
import pers.prover07.yygh.user.service.PatientService;
import pers.prover07.yygh.user.service.UserInfoService;
import pers.prover07.yygh.vo.user.LoginVo;
import pers.prover07.yygh.vo.user.UserAuthVo;
import pers.prover07.yygh.vo.user.UserInfoQueryVo;

import java.util.*;

/**
 * @Classname UserInfoServiceImpl
 * @Description TODO
 * @Date 2021/12/1 14:15
 * @Created by Prover07
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private PatientService patientService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        // 验证码
        String code = loginVo.getCode();
        // 手机号
        String phone = loginVo.getPhone();
        // 获取微信 openid
        String openid = loginVo.getOpenid();

        // 健壮性
        if (StringUtils.isBlank(code) || StringUtils.isBlank(phone)) {
            throw new BaseServiceException(ResultCodeEnum.DATA_ERROR);
        }

        // 验证码比对
        String sendCode = (String) redisTemplate.opsForValue().get(RedisKeyConstant.SMS_PHONE_CODE + phone);
        if (StringUtils.isBlank(sendCode)) {
            throw new BaseServiceException(ResultCodeEnum.CODE_ERROR);
        }
        UserInfo userInfo;
        // 判断是否未微信登录(需要绑定手机号)
        if (StringUtils.isBlank(openid)) {
            /*
             * 不为微信登录 -> 走正常手机号登录流程
             *       - 判断用户是否为第一次登录，如果为第一次登录则进行注册
             * */
            userInfo = this.getOne(new QueryWrapper<UserInfo>().eq("phone", phone));
            if (userInfo == null) {
                userInfo = new UserInfo();
                userInfo.setName(phone);
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                this.save(userInfo);
            }
        } else {
            // 为微信登录 - 绑定手机号
            userInfo = this.getByOpenid(openid);
            if (userInfo == null) {
                throw new BaseServiceException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }
            userInfo.setPhone(phone);
            this.updateById(userInfo);
        }

        // 如果用户状态被锁定就不能使用
        if (userInfo.getStatus() == 0) {
            throw new BaseServiceException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        // 获取 JwtToken
        String jwtToken = JwtUtil.createJwtToken(userInfo.getId(), userInfo.getName());

        // 封装数据返回
        return Dict.create()
                .set("name", userInfo.getName())
                .set("token", jwtToken);
    }

    @Override
    public UserInfo getByOpenid(String openid) {
        return this.getOne(new QueryWrapper<UserInfo>().eq("openid", openid));
    }

    @Override
    public void authUser(String userId, UserAuthVo userAuthVo) {
        // 获取用户信息
        UserInfo userInfo = this.getById(userId);

        // 更新信息
        BeanUtil.copyProperties(userAuthVo, userInfo);
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());

        // 更新用户信息
        this.updateById(userInfo);
    }

    @Override
    public Page<UserInfo> selectPage(Page<UserInfo> userInfoPage, UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> page = this.page(userInfoPage,
                              // 根据条件对象 userInfoQueryVo 自动构建 qw
                              QueryWrapperUtil.getInstance().wrapper(userInfoQueryVo, new QueryWrapper<UserInfo>()));
        page.getRecords().forEach(this::packInfo);
        return page;
    }

    @Override
    public void lockUser(String userId, Integer status) {
        UserInfo userInfo = this.getById(userId);
        // 提高健壮性
        if (userInfo == null || !CollUtil.newArrayList(0, 1).contains(status)) {
            throw new BaseServiceException(ResultCodeEnum.PARAM_ERROR);
        }
        userInfo.setStatus(status);
        baseMapper.updateById(userInfo);
    }

    @Override
    public Map<String, Object> getDetailInfo(String userId) {
        // 获取用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        this.packInfo(userInfo);

        // 获取就诊人信息
        List<Patient> patientList = patientService.listByUserId(userId);

        return new HashMap<String, Object>(2){{
            put("userInfo", userInfo);
            put("patientList", patientList);
        }};
    }

    @Override
    public void approval(String userId, Integer authStatus) {
        UserInfo userInfo = this.getById(userId);
        if (userInfo == null || !CollUtil.newArrayList(-1,2).contains(authStatus)) {
            throw new BaseServiceException(ResultCodeEnum.PARAM_ERROR);
        }
        userInfo.setAuthStatus(authStatus);
        this.updateById(userInfo);
    }

    /**
     * 封装数据
     * @param userInfo
     */
    private void packInfo(UserInfo userInfo) {
        // 获取认证状态
        String authStatusStr = AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus());

        // 获取用户状态
        String statusStr = userInfo.getStatus() == 0 ? "锁定" : "正常";

        userInfo.getParams().putAll(new HashMap<String, String>(){{
            put("authStatusStr", authStatusStr);
            put("statusStr", statusStr);
        }});
    }
}
