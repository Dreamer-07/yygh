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
        // ??????????????????
        ScheduleOrderVo scheduleOrderVo = hospFeignClient.getScheduleDetailInfoInner(scheduleId);
        // ?????????????????????
        Patient patient = userPatientFeignClient.getInfoByIdInner(patientId);

        // ??????????????????????????????????????????
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

        // TODO: ??????????????????
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
            //??????????????????????????????????????????????????????
            String hosRecordId = jsonObject.getString("hosRecordId");
            //????????????
            Integer number = jsonObject.getInteger("number");;
            //????????????
            String fetchTime = jsonObject.getString("fetchTime");;
            //????????????
            String fetchAddress = jsonObject.getString("fetchAddress");;
            //????????????
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.updateById(orderInfo);

            //??????????????????
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //?????????????????????
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            //??????mq?????????????????????????????????
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
        // ??????????????????
        OrderInfo orderInfo = this.getById(orderId);
        if (orderInfo == null) {
            throw new BaseServiceException(ResultCodeEnum.PARAM_ERROR);
        }
        // ????????????
        DateTime quitTime = DateTime.of(orderInfo.getQuitTime());
        if (quitTime.isBefore(new Date())) {
            throw new BaseServiceException(ResultCodeEnum.CANCEL_ORDER_FAIL);
        }
        // ??????????????????
        SignInfoVo signInfoVo = hospFeignClient.getHospSignInfo(orderInfo.getHoscode());
        Map<String, Object> requestMap = new HashMap<String, Object>(){{
            put("hoscode",orderInfo.getHoscode());
            put("hosRecordId",orderInfo.getHosRecordId());
            put("timestamp", HttpRequestHelper.getTimestamp());
            put("sign", HttpRequestHelper.getSign(this, signInfoVo.getSignKey()));
        }};
        // ????????????????????????????????????
        JSONObject result = HttpRequestHelper.sendRequest(requestMap, signInfoVo.getApiUrl() + "/order/updateCancelStatus");
        if (result.getInteger("code") != 200) {
            throw new BaseServiceException(ResultCodeEnum.PARAM_ERROR);
        }
        // ???????????????????????????????????????
        if (orderInfo.getOrderStatus().intValue() == OrderStatusEnum.PAID.getStatus()) {
            // ????????????????????????
            boolean isRefund = wxPayService.refund(orderId);
            if (isRefund) {
                // ??????????????????
                orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
                this.updateById(orderInfo);
                // TODO: ?????? hosp ????????????????????????(????????????hhh)
                return true;
            }
        }
        return false;
    }

    @Override
    public void medicalNotice() {
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("reserve_date", DateUtil.format(new Date(), "yyyy-MM-dd"));
        // ??????????????????
        queryWrapper.ne("order_status", OrderStatusEnum.CANCLE.getStatus());
        List<OrderInfo> orderInfoList = this.list(queryWrapper);
        for (OrderInfo orderInfo : orderInfoList) {
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            // TODO: ?????? mq ??????????????????
            System.out.println(msmVo);
        }
    }

    @Override
    public Map<String, List> countOrderInfo(OrderCountQueryVo countQueryVo) {
        List<OrderCountVo> orderCountVoList = baseMapper.countByReserveDate(countQueryVo);

        // ??????x???(??????) ??????
        List<String> dateList = orderCountVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());

        // ??????y???(??????) ??????
        List<Integer> countList = orderCountVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());


        return new HashMap<String, List>(){{
            put("dateList", dateList);
            put("countList", countList);
        }};
    }

    /**
     * ??????????????????
     * @param orderInfo
     * @return
     */
    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        String orderStatusName = OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus());
        orderInfo.getParams().put("orderStatusString", orderStatusName);
        return orderInfo;
    }

}
