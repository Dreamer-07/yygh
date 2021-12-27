package pers.prover07.yygh.hosp.service;

import org.springframework.data.domain.Page;
import pers.prover07.yygh.model.hosp.Schedule;
import pers.prover07.yygh.vo.hosp.ScheduleQueryVo;

import java.util.List;
import java.util.Map;

/**
 * @Classname ScheduleService
 * @Description TODO
 * @Date 2021/11/25 19:17
 * @Created by Prover07
 */
public interface ScheduleService {
    Page<Schedule> getPageSchedule(Integer page, Integer pageSize, ScheduleQueryVo scheduleQueryVo);

    void saveSchedule(Map<String, Object> dataMap);

    void removeSchedule(String hoscode, String hosScheduleId);

    Map<String, Object> getRuleSchedule(String hoscode, String depcode, long page, long limit);

    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    /**
     * 获取可预约的排榜信息
     * @param hoscode
     * @param depcode
     * @param page
     * @param limit
     * @return
     */
    Map<String, Object> getBookingSchedule(String hoscode, String depcode, Integer page, Integer limit);

    /**
     * 根据 id 获取 schedule
     * @param scheduleId
     * @return
     */
    Schedule getScheduleById(String scheduleId);

    /**
     * 修改 schedule stock(预约数量) 信息
     * @param schedule
     */
    void updateStock(Schedule schedule);
}
