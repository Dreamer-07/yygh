package pers.prover07.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import pers.prover07.yygh.enums.RefundStatusEnum;
import pers.prover07.yygh.model.order.PaymentInfo;
import pers.prover07.yygh.model.order.RefundInfo;
import pers.prover07.yygh.order.mapper.RefundMapper;
import pers.prover07.yygh.order.service.RefundService;

/**
 * @author by Prover07
 * @classname RefundServiceImpl
 * @description TODO
 * @date 2021/12/13 20:11
 */
@Service
public class RefundServiceImpl extends ServiceImpl<RefundMapper, RefundInfo> implements RefundService {
    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
        QueryWrapper<RefundInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", paymentInfo.getOrderId());
        queryWrapper.eq("payment_type", paymentInfo.getPaymentType());
        RefundInfo refundInfo = this.getOne(queryWrapper);
        if (refundInfo != null) {
            return refundInfo;
        }
        refundInfo = new RefundInfo();
        BeanUtils.copyProperties(paymentInfo, refundInfo);
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
        refundInfo.setId(null);
        this.save(refundInfo);
        return refundInfo;
    }
}
