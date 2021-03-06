package pers.prover07.yygh.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import pers.prover07.yygh.mapper.OrderInfoMapper;
import pers.prover07.yygh.mapper.ScheduleMapper;
import pers.prover07.yygh.model.OrderInfo;
import pers.prover07.yygh.model.Patient;
import pers.prover07.yygh.model.Schedule;
import pers.prover07.yygh.service.ApiService;
import pers.prover07.yygh.service.HospitalService;
import pers.prover07.yygh.util.HttpRequestHelper;
import pers.prover07.yygh.util.ResultCodeEnum;
import pers.prover07.yygh.util.YyghException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Service
@Slf4j
public class HospitalServiceImpl implements HospitalService {

	@Autowired
	private ScheduleMapper hospitalMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> submitOrder(Map<String, Object> paramMap) {
        log.info(JSONObject.toJSONString(paramMap));
        String hoscode = (String)paramMap.get("hoscode");
        String depcode = (String)paramMap.get("depcode");
        String hosScheduleId = (String)paramMap.get("hosScheduleId");
        String reserveDate = (String)paramMap.get("reserveDate");
        String reserveTime = (String)paramMap.get("reserveTime");
        String amount = (String)paramMap.get("amount");

        Schedule schedule = this.getSchedule(hosScheduleId);
        if(null == schedule) {
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }

        if(!schedule.getHoscode().equals(hoscode)
                || !schedule.getDepcode().equals(depcode)
                || !schedule.getAmount().equals(amount)) {
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        //???????????????
        Patient patient = JSONObject.parseObject((String) paramMap.get("patient"), Patient.class);
        log.info(JSONObject.toJSONString(patient));
        //?????????????????????
        String patientId = this.savePatient(patient);

        Map<String, Object> resultMap = new HashMap<>();
        int availableNumber = schedule.getAvailableNumber().intValue() - 1;
        if(availableNumber > 0) {
            schedule.setAvailableNumber(availableNumber);
            hospitalMapper.updateById(schedule);

            //??????????????????
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setPatientId(patientId);
            orderInfo.setScheduleId(hosScheduleId);
            int number = schedule.getReservedNumber().intValue() - schedule.getAvailableNumber().intValue();
            orderInfo.setNumber(number);
            orderInfo.setAmount(new BigDecimal(amount));
            String fetchTime = "0".equals(reserveDate) ? " 09:30???" : " 14:00???";
            orderInfo.setFetchTime(reserveTime + fetchTime);
            orderInfo.setFetchAddress("??????9?????????");
            //?????? ?????????
            orderInfo.setOrderStatus(0);
            orderInfoMapper.insert(orderInfo);

            resultMap.put("resultCode","0000");
            resultMap.put("resultMsg","????????????");
            //??????????????????????????????????????????????????????
            resultMap.put("hosRecordId", orderInfo.getId());
            //????????????
            resultMap.put("number", number);
            //????????????
            resultMap.put("fetchTime", reserveDate + "09:00???");;
            //????????????
            resultMap.put("fetchAddress", "??????114??????");;
            //??????????????????
            resultMap.put("reservedNumber", schedule.getReservedNumber());
            //?????????????????????
            resultMap.put("availableNumber", schedule.getAvailableNumber());
        } else {
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        return resultMap;
    }

    @Override
    public void updatePayStatus(Map<String, Object> paramMap) {
        String hoscode = (String)paramMap.get("hoscode");
        String hosRecordId = (String)paramMap.get("hosRecordId");

        OrderInfo orderInfo = orderInfoMapper.selectById(hosRecordId);
        if(null == orderInfo) {
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        //?????????
        orderInfo.setOrderStatus(1);
        orderInfo.setPayTime(new Date());
        orderInfoMapper.updateById(orderInfo);
    }

    @Override
    public void updateCancelStatus(Map<String, Object> paramMap) {
        String hoscode = (String)paramMap.get("hoscode");
        String hosRecordId = (String)paramMap.get("hosRecordId");

        OrderInfo orderInfo = orderInfoMapper.selectById(hosRecordId);
        if(null == orderInfo) {
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        //?????????
        orderInfo.setOrderStatus(-1);
        orderInfo.setQuitTime(new Date());
        orderInfoMapper.updateById(orderInfo);
    }

    private Schedule getSchedule(String frontSchId) {
        return hospitalMapper.selectById(frontSchId);
    }

    /**
     * ???????????????????????????
     * @param patient
     */
    private String savePatient(Patient patient) {
        // ????????????
        return patient.getId();
    }


}
