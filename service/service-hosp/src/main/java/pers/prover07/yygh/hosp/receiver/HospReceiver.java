package pers.prover07.yygh.hosp.receiver;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.prover07.yygh.hosp.service.ScheduleService;
import pers.prover07.yygh.model.hosp.Schedule;
import pers.prover07.yygh.rabbit.util.RabbitUtil;
import pers.prover07.yygh.rabbit.util.constant.MqConstant;
import pers.prover07.yygh.vo.hosp.ScheduleStockVo;
import pers.prover07.yygh.vo.msm.MsmVo;

/**
 * @author by Prover07
 * @classname HospReceiver
 * @description TODO
 * @date 2021/12/12 21:23
 */
@Component
public class HospReceiver {

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private RabbitUtil rabbitUtil;


    /**
     * {@code @RabbitListener} - 负责定义监听的 exchange 和 queue 以及对应的 routingkey
     * @param scheduleStockVo
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(MqConstant.EXCHANGE_HOSP),
            value = @Queue(MqConstant.QUEUE_HOSP_STOCK),
            key = MqConstant.ROUTINGKEY_HOSP_STOCK
    ))
    public void updateScheduleStock(ScheduleStockVo scheduleStockVo) {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleStockVo, schedule);
        MsmVo msmVo = scheduleStockVo.getMsmVo();
        scheduleService.updateStock(schedule);
        if (msmVo != null) {
            rabbitUtil.sendMessage(MqConstant.EXCHANGE_SMS, MqConstant.ROUTINGKEY_SMS_SEND, msmVo);
        }
    }

}
