package pers.prover07.yygh.hosp.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.hosp.service.ScheduleService;
import pers.prover07.yygh.model.hosp.Schedule;

import java.util.List;
import java.util.Map;

/**
 * @Classname ScheduleController
 * @Description TODO
 * @Date 2021/11/29 22:34
 * @Created by Prover07
 */
@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation("获取排班规则信息")
    @GetMapping("/getScheduleRule/{hoscode}/{depcode}/{page}/{limit}")
    public Result<Map<String, Object>> getScheduleRule(@PathVariable String hoscode, @PathVariable String depcode,
                                                        @PathVariable long page, @PathVariable long limit){
        Map<String, Object> dataMap = scheduleService.getRuleSchedule(hoscode, depcode, page, limit);
        return Result.ok(dataMap);
    }

    @ApiOperation("获取排班详细信息")
    @GetMapping("/getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result<List<Schedule>> getScheduleDetail(@PathVariable String hoscode,
                                                    @PathVariable String depcode,
                                                    @PathVariable String workDate){
        List<Schedule> schedules = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return Result.ok(schedules);
    }

}
