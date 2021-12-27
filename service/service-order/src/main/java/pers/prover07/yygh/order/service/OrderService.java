package pers.prover07.yygh.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.prover07.yygh.model.order.OrderInfo;
import pers.prover07.yygh.vo.order.OrderCountQueryVo;
import pers.prover07.yygh.vo.order.OrderCountVo;
import pers.prover07.yygh.vo.order.OrderQueryVo;

import java.util.List;
import java.util.Map;

/**
 * @author by Prover07
 * @classname OrderService
 * @description TODO
 * @date 2021/12/9 13:03
 */
public interface OrderService extends IService<OrderInfo> {

    /**
     * 保存订单信息
     * @param patientId
     * @param scheduleId
     * @return
     */
    String saveOrderInfo(String patientId, String scheduleId);

    /**
     * 根据订单 id 获取订单详情
     * @param orderId
     * @return
     */
    OrderInfo getDetailById(String orderId);

    /**
     * 获取用户对应的所有订单列表
     * @param iPage
     * @param orderQueryVo
     * @return
     */
    IPage<OrderInfo> getOrderInfoByUser(Page<OrderInfo> iPage, OrderQueryVo orderQueryVo);

    /**
     * 取消指定订单的付款
     * @param orderId
     * @return
     */
    boolean cancelPayment(String orderId) throws Exception;

    /**
     * 通知就医
     */
    void medicalNotice();

    /**
     * 统计订单信息
     * @param countQueryVo
     * @return
     */
    Map<String, List> countOrderInfo(OrderCountQueryVo countQueryVo);
}
