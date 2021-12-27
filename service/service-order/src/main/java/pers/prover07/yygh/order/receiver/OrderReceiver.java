package pers.prover07.yygh.order.receiver;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.prover07.yygh.order.service.OrderService;
import pers.prover07.yygh.rabbit.util.constant.MqConstant;

/**
 * @author by Prover07
 * @classname OrderReceiver
 * @description TODO
 * @date 2021/12/14 9:38
 */
@Component
public class OrderReceiver {

    @Autowired
    private OrderService orderService;

    /**
     * 定时通知就医
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(MqConstant.EXCHANGE_TASK),
            value = @Queue(MqConstant.QUEUE_TASK_ORDER_NOTICE),
            key = MqConstant.ROUTINGKEY_TASK_ORDER_NOTICE
    ))
    public void medicalNotice() {
        orderService.medicalNotice();
    }

}
