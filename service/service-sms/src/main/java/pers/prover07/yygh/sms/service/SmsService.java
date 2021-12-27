package pers.prover07.yygh.sms.service;

import pers.prover07.yygh.vo.msm.MsmVo;

/**
 * @Classname SmsService
 * @Description TODO
 * @Date 2021/12/1 20:01
 * @Created by Prover07
 */
public interface SmsService {
    /**
     * 发送验证码到指定手机
     * @param phone
     * @return
     */
    boolean send(String phone);

    /**
     * 发送来自 AMQP 队列的消息
     * @param msmVo
     */
    boolean sendForAmqp(MsmVo msmVo);
}
