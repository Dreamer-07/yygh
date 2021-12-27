package pers.prover07.yygh.rabbit.util.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author by Prover07
 * @classname MqConstrant
 * @description TODO
 * @date 2021/12/10 11:04
 */
public class MqConstant {

    public final static String EXCHANGE_SMS = "exchange.direct.yygh.msm";
    public final static String EXCHANGE_HOSP = "exchange.direct.yygh.hosp";
    public final static String EXCHANGE_TASK = "exchange.direct.yygh.task";

    public final static String QUEUE_SMS_SEND = "queue.yygh.sms.send";
    public final static String QUEUE_HOSP_STOCK = "queue.yygh.hosp.stock";
    public final static String QUEUE_TASK_ORDER_NOTICE = "queue.yygh.order.notice";

    public final static String ROUTINGKEY_SMS_SEND = "yygh.sms.send";
    public final static String ROUTINGKEY_HOSP_STOCK = "yygh.hosp.stock";
    public final static String ROUTINGKEY_TASK_ORDER_NOTICE = "yygh.order.notice";

}
