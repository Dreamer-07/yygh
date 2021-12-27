package pers.prover07.yygh.sms.receiver;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.prover07.yygh.rabbit.util.constant.MqConstant;
import pers.prover07.yygh.sms.service.SmsService;
import pers.prover07.yygh.vo.msm.MsmVo;

/**
 * @author by Prover07
 * @classname SmsReciver
 * @description sms 模块 rabbitmq 监听器
 * @date 2021/12/10 13:51
 */
@Component
public class SmsReceiver {

    @Autowired
    private SmsService smsService;

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(MqConstant.EXCHANGE_SMS),
            value = @Queue(MqConstant.QUEUE_SMS_SEND),
            key = MqConstant.ROUTINGKEY_SMS_SEND
    ))
    public void send(MsmVo msmVo) {
        smsService.sendForAmqp(msmVo);
    }

}
