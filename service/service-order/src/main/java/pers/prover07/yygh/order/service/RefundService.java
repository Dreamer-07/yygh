package pers.prover07.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.prover07.yygh.model.order.PaymentInfo;
import pers.prover07.yygh.model.order.RefundInfo;

/**
 * @author by Prover07
 * @classname RefundService
 * @description TODO
 * @date 2021/12/13 20:11
 */
public interface RefundService extends IService<RefundInfo> {
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}
