package pers.prover07.yygh.order.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pers.prover07.yygh.common.util.exception.BaseServiceException;
import pers.prover07.yygh.common.util.helper.HttpRequestHelper;
import pers.prover07.yygh.common.util.result.ResultCodeEnum;
import pers.prover07.yygh.enums.OrderStatusEnum;
import pers.prover07.yygh.model.order.OrderInfo;
import pers.prover07.yygh.model.user.Patient;
import pers.prover07.yygh.order.feign.HospFeignClient;
import pers.prover07.yygh.order.feign.UserPatientFeignClient;
import pers.prover07.yygh.order.mapper.OrderMapper;
import pers.prover07.yygh.order.service.OrderService;
import pers.prover07.yygh.order.service.WxPayService;
import pers.prover07.yygh.rabbit.util.RabbitUtil;
import pers.prover07.yygh.rabbit.util.constant.MqConstant;
import pers.prover07.yygh.service.util.QueryWrapperUtil;
import pers.prover07.yygh.vo.hosp.ScheduleOrderVo;
import pers.prover07.yygh.vo.hosp.ScheduleStockVo;
import pers.prover07.yygh.vo.msm.MsmVo;
import pers.prover07.yygh.vo.order.OrderCountQueryVo;
import pers.prover07.yygh.vo.order.OrderCountVo;
import pers.prover07.yygh.vo.order.OrderQueryVo;
import pers.prover07.yygh.vo.order.SignInfoVo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author by Prover07
 * @classname OrderServiceImpl
 * @description TODO
 * @date 2021/12/9 13:05
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderInfo> implements OrderService {

    @Autowired
    private HospFeignClient hospFeignClient;
    @Autowired
    private UserPatientFeignClient userPatientFeignClient;

    @Autowired
    private RabbitUtil rabbitUtil;

    @Autowired
    private WxPayService wxPayService;

    @Override
    public String saveOrderInfo(String patientId, String scheduleId) {
        // 获取科室信息
        ScheduleOrderVo scheduleOrderVo = hospFeignClient.getScheduleDetailInfoInner(scheduleId);
        // 获取就诊人信息
        Patient patient = userPatientFeignClient.getInfoByIdInner(patientId);

        // 如果超过了放号时间就直接返回
        Date startTime = scheduleOrderVo.getStartTime();
        Date endTime = scheduleOrderVo.getEndTime();
        if (DateTime.of(startTime).isAfter(new Date()) || DateTime.of(endTime).isBefore(new Date())) {
            throw new BaseServiceException(ResultCodeEnum.TIME_NO);
        }

        String hoscode = scheduleOrderVo.getHoscode();
        SignInfoVo hospSignInfo = hospFeignClient.getHospSignInfo(hoscode);
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
        String orderId = IdUtil.fastSimpleUUID();
        orderInfo.setOutTradeNo(orderId);
        orderInfo.setScheduleId(scheduleId);
        orderInfo.setUserId(patient.getUserId());

        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        this.save(orderInfo);

        // TODO: ???懒得优化了
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("patient", JSONObject.toJSONString(patient));
        dataMap.put("hoscode", orderInfo.getHoscode());
        dataMap.put("depcode", orderInfo.getDepcode());
        dataMap.put("hosScheduleId", scheduleId);
        dataMap.put("reserveDate", new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        dataMap.put("reserveTime", orderInfo.getReserveTime());
        dataMap.put("amount", orderInfo.getAmount());
        dataMap.put("timestamp", HttpRequestHelper.getTimestamp());
        dataMap.put("sign", HttpRequestHelper.getSign(dataMap, hospSignInfo.getSignKey()));

        JSONObject result = HttpRequestHelper.sendRequest(dataMap, hospSignInfo.getApiUrl() + "/order/submitOrder");
        if(result.getInteger("code") == 200) {
            JSONObject jsonObject = result.getJSONObject("data");
            //预约记录唯一标识（医院预约记录主键）
            String hosRecordId = jsonObject.getString("hosRecordId");
            //预约序号
            Integer number = jsonObject.getInteger("number");;
            //取号时间
            String fetchTime = jsonObject.getString("fetchTime");;
            //取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");;
            //更新订单
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.updateById(orderInfo);

            //排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            //发送mq信息更新号源和短信通知
            ScheduleStockVo scheduleStockVo = new ScheduleStockVo();
            scheduleStockVo.setScheduleId(scheduleId);
            scheduleStockVo.setReservedNumber(reservedNumber);
            scheduleStockVo.setAvailableNumber(availableNumber);
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(patient.getPhone());
            scheduleStockVo.setMsmVo(msmVo);
            rabbitUtil.sendMessage(MqConstant.EXCHANGE_HOSP, MqConstant.QUEUE_HOSP_STOCK, scheduleStockVo);
        } else {
            throw new BaseServiceException(ResultCodeEnum.FAIL.getCode(), result.getString("message"));
        }
        return orderInfo.getId();
    }

    @Override
    public OrderInfo getDetailById(String orderId) {
        OrderInfo orderInfo = this.getById(orderId);
        return this.packOrderInfo(orderInfo);
    }

    @Override
    public IPage<OrderInfo> getOrderInfoByUser(Page<OrderInfo> iPage, OrderQueryVo orderQueryVo) {
        IPage<OrderInfo> page = this.page(iPage, QueryWrapperUtil.getInstance().wrapper(orderQueryVo, new QueryWrapper<OrderInfo>()));
        page.getRecords().forEach(this::packOrderInfo);
        return page;
    }

    @Override
    public boolean cancelPayment(String orderId) throws Exception {
        // 获取订单信息
        OrderInfo orderInfo = this.getById(orderId);
        if (orderInfo == null) {
            throw new BaseServiceException(ResultCodeEnum.PARAM_ERROR);
        }
        // 判断时间
        DateTime quitTime = DateTime.of(orderInfo.getQuitTime());
        if (quitTime.isBefore(new Date())) {
            throw new BaseServiceException(ResultCodeEnum.CANCEL_ORDER_FAIL);
        }
        // 获取医院信息
        SignInfoVo signInfoVo = hospFeignClient.getHospSignInfo(orderInfo.getHoscode());
        Map<String, Object> requestMap = new HashMap<String, Object>(){{
            put("hoscode",orderInfo.getHoscode());
            put("hosRecordId",orderInfo.getHosRecordId());
            put("timestamp", HttpRequestHelper.getTimestamp());
            put("sign", HttpRequestHelper.getSign(this, signInfoVo.getSignKey()));
        }};
        // 调用医院接口实现预约取消
        JSONObject result = HttpRequestHelper.sendRequest(requestMap, signInfoVo.getApiUrl() + "/order/updateCancelStatus");
        if (result.getInteger("code") != 200) {
            throw new BaseServiceException(ResultCodeEnum.PARAM_ERROR);
        }
        // 如果订单还未支付就不能退款
        if (orderInfo.getOrderStatus().intValue() == OrderStatusEnum.PAID.getStatus()) {
            // 调用微信退款方法
            boolean isRefund = wxPayService.refund(orderId);
            if (isRefund) {
                // 更新订单状态
                orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
                this.updateById(orderInfo);
                // TODO: 通知 hosp 模块更新可预约数(懒的敲了hhh)
                return true;
            }
        }
        return false;
    }

    @Override
    public void medicalNotice() {
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("reserve_date", DateUtil.format(new Date(), "yyyy-MM-dd"));
        // 支持线下支付
        queryWrapper.ne("order_status", OrderStatusEnum.CANCLE.getStatus());
        List<OrderInfo> orderInfoList = this.list(queryWrapper);
        for (OrderInfo orderInfo : orderInfoList) {
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            // TODO: 通过 mq 发送短信通知
            System.out.println(msmVo);
        }
    }

    @Override
    public Map<String, List> countOrderInfo(OrderCountQueryVo countQueryVo) {
        List<OrderCountVo> orderCountVoList = baseMapper.countByReserveDate(countQueryVo);

        // 获取x轴(日期) 数据
        List<String> dateList = orderCountVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());

        // 获取y轴(数量) 数据
        List<Integer> countList = orderCountVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());


        return new HashMap<String, List>(){{
            put("dateList", dateList);
            put("countList", countList);
        }};
    }

    /**
     * 封装订单信息
     * @param orderInfo
     * @return
     */
    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        String orderStatusName = OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus());
        orderInfo.getParams().put("orderStatusString", orderStatusName);
        return orderInfo;
    }

}
