package pers.prover07.yygh.rabbit.util;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author by Prover07
 * @classname RabbitUtil
 * @description TODO
 * @date 2021/12/10 13:47
 */
@Component
public class RabbitUtil {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 向 rabbitmq 中的指定 exchange 中对应的 routingKey 发送 message
     * @param exchange
     * @param routingKey
     * @param message
     * @return
     */
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }

}
