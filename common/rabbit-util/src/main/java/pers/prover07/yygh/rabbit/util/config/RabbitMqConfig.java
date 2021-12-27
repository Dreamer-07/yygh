package pers.prover07.yygh.rabbit.util.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;

/**
 * @author by Prover07
 * @classname RabbitMqConfig
 * @description RabbitMQ 配置类
 * @date 2021/12/10 13:39
 */
@Controller
public class RabbitMqConfig {

    /**
     * 将 rabbitmq 中数据序列化的方式转换成 JSON 格式的
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
