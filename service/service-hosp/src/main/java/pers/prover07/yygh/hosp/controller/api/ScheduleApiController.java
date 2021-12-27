package pers.prover07.yygh.hosp.controller.api;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.hosp.service.DepartmentService;
import pers.prover07.yygh.hosp.service.HospitalService;
import pers.prover07.yygh.hosp.service.ScheduleService;
import pers.prover07.yygh.model.hosp.BookingRule;
import pers.prover07.yygh.model.hosp.Hospital;
import pers.prover07.yygh.model.hosp.Schedule;
import pers.prover07.yygh.vo.hosp.ScheduleOrderVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Classname ScheduleApiController
 * @Description TODO
 * @Date 2021/12/7 16:12
 * @Created by Prover07
 */
@RestController
@RequestMapping("/api/hosp/schedule")
public class ScheduleApiController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentServicel;

    @ApiOperation("获取可预约排班信息")
    @GetMapping("/getBookingScheduleRule/{hoscode}/{depcode}/{page}/{limit}")
    public Result<Map<String, Object>> getBookingSchedule(@PathVariable String hoscode, @PathVariable String depcode,
                                                          @PathVariable Integer page, @PathVariable Integer limit) {
        Map<String, Object> dataMap = scheduleService.getBookingSchedule(hoscode, depcode, page, limit);
        return Result.ok(dataMap);
    }

    @ApiOperation("获取指定日期的排班信息")
    @GetMapping("/getScheduleList/{hoscode}/{depcode}/{workDate}")
    public Result<List<Schedule>> getScheduleByWorkDate(@PathVariable String hoscode, @PathVariable String depcode,
                                                        @PathVariable String workDate) {
        List<Schedule> schedules = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return Result.ok(schedules);
    }

    @ApiOperation("获取科室信息")
    @GetMapping("/getScheduleInfo/{scheduleId}")
    public Result<Schedule> getScheduleDetailInfo(@PathVariable String scheduleId) {
        Schedule schedule = scheduleService.getScheduleById(scheduleId);
        return Result.ok(schedule);
    }

    @ApiOperation("远程调用 - 获取科室信息")
    @GetMapping("/inner/getScheduleInfo/{scheduleId}")
    public ScheduleOrderVo getScheduleDetailInfoInner(@PathVariable("scheduleId") String scheduleId) {
        Schedule schedule = scheduleService.getScheduleById(scheduleId);

        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();

        return this.packScheduleOrderVo(schedule, scheduleOrderVo);
    }

    /**
     * 向 scheduleOrderVo 中封装 schedule 数据
     * @param schedule
     * @param scheduleOrderVo
     * @return
     */
    private ScheduleOrderVo packScheduleOrderVo(Schedule schedule, ScheduleOrderVo scheduleOrderVo) {
        BeanUtils.copyProperties(schedule, scheduleOrderVo);

        // 设置 医院名 和 科室名
        String hoscode = scheduleOrderVo.getHoscode();
        String depcode = scheduleOrderVo.getDepcode();
        String depname = departmentServicel.getDepartmentByHoscodeAndDepcode(hoscode, depcode).getDepname();
        Hospital hospital = hospitalService.getHospitalByHoscode(hoscode);
        String hosname = hospital.getHosname();
        scheduleOrderVo.setHosname(hosname);
        scheduleOrderVo.setDepname(depname);

        // 设置科室的工作日期和时间
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());

        // 获取预约规则
        BookingRule bookingRule = hospital.getBookingRule();
        // 设置退号时间
        Integer quitDay = bookingRule.getQuitDay();
        scheduleOrderVo.setQuitTime(this.getDateTime(
                DateUtil.offsetDay(schedule.getWorkDate(), quitDay),
                bookingRule.getQuitTime()));

        // 设置当天预约开始/关闭时间
        scheduleOrderVo.setStartTime(this.getDateTime(new Date(), bookingRule.getReleaseTime()));
        scheduleOrderVo.setStopTime(this.getDateTime(new Date(), bookingRule.getStopTime()));

        // 设置挂号结束时间
        Integer cycle = bookingRule.getCycle();
        scheduleOrderVo.setEndTime(this.getDateTime(
                DateUtil.offsetDay(schedule.getWorkDate(), cycle),
                bookingRule.getStopTime()));

        return scheduleOrderVo;
    }



    /**
     * 将 Date 与 timeString 拼接并转换为 DateTime
     *
     * @param date
     * @param timeString
     * @return
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateString = DateUtil.format(date, "yyyy-MM-dd ");
        return DateTime.of(dateString + timeString, "yyyy-MM-dd HH:mm");
    }
}
