package pers.prover07.yygh.task.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pers.prover07.yygh.rabbit.util.RabbitUtil;
import pers.prover07.yygh.rabbit.util.constant.MqConstant;

/**
 * @author by Prover07
 * @classname OrderTask
 * @description TODO
 * @date 2021/12/14 9:31
 */
@Component
@EnableScheduling
public class OrderTask {

    @Autowired
    private RabbitUtil rabbitUtil;

    /**
     * 定时就医通知
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void medicalNotice() {
        rabbitUtil.sendMessage(MqConstant.EXCHANGE_TASK, MqConstant.ROUTINGKEY_TASK_ORDER_NOTICE, "");
    }

}
