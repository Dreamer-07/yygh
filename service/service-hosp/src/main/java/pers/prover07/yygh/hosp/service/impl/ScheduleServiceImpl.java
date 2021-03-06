package pers.prover07.yygh.hosp.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.lang.Dict;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import pers.prover07.yygh.common.util.exception.BaseServiceException;
import pers.prover07.yygh.common.util.result.ResultCodeEnum;
import pers.prover07.yygh.hosp.repository.ScheduleRepository;
import pers.prover07.yygh.hosp.service.DepartmentService;
import pers.prover07.yygh.hosp.service.HospitalService;
import pers.prover07.yygh.hosp.service.ScheduleService;
import pers.prover07.yygh.model.hosp.BookingRule;
import pers.prover07.yygh.model.hosp.Department;
import pers.prover07.yygh.model.hosp.Hospital;
import pers.prover07.yygh.model.hosp.Schedule;
import pers.prover07.yygh.vo.hosp.BookingScheduleRuleVo;
import pers.prover07.yygh.vo.hosp.ScheduleQueryVo;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Classname ScheduleServiceImpl
 * @Description TODO
 * @Date 2021/11/25 19:17
 * @Created by Prover07
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;

    @PostConstruct
    public void initIndex() {
        mongoTemplate.indexOps(Schedule.class).ensureIndex(new Index("hoscode", Sort.Direction.ASC));
        mongoTemplate.indexOps(Schedule.class).ensureIndex(new Index("depcode", Sort.Direction.ASC));
        mongoTemplate.indexOps(Schedule.class).ensureIndex(new Index("hosScheduleId", Sort.Direction.ASC));
    }

    @Override
    public Page<Schedule> getPageSchedule(Integer page, Integer pageSize, ScheduleQueryVo scheduleQueryVo) {
        // ??????????????????
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        // ??????????????????????????????
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        // ??????????????????
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        Example<Schedule> scheduleExample = Example.of(schedule, exampleMatcher);

        return scheduleRepository.findAll(scheduleExample, pageable);
    }

    @Override
    public void saveSchedule(Map<String, Object> dataMap) {
        // ??????????????????
        Schedule schedule = JSON.parseObject(JSON.toJSONString(dataMap), Schedule.class);

        // ??????????????????
        Schedule existsSchedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());

        if (existsSchedule != null) {
            // ???????????? - ?????????
            schedule.setCreateTime(existsSchedule.getCreateTime());
        } else {
            schedule.setCreateTime(new Date());
        }
        schedule.setUpdateTime(new Date());
        schedule.setIsDeleted(0);

        scheduleRepository.save(schedule);
    }

    @Override
    public void removeSchedule(String hoscode, String hosScheduleId) {
        // ????????????????????????
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);

        if (schedule != null) {
            scheduleRepository.deleteById(schedule.getId());
            return;
        }

        throw new BaseServiceException(ResultCodeEnum.FAIL);
    }

    @Override
    public Map<String, Object> getRuleSchedule(String hoscode, String depcode, long page, long limit) {
        // ??????????????????
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        // ????????????????????????
        Aggregation aggregation = Aggregation.newAggregation(
                // ??????????????????
                Aggregation.match(criteria),

                // ??????????????????, ?????? workDate ??????????????????
                Aggregation.group("workDate")
                        // ??????????????????????????????
                        .first("workDate").as("workDate")
                        // ???????????????????????????????????????
                        .count().as("docCount")
                        // ??????????????????????????????????????????
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),

                // ??????(???????????? + ????????????)
                Aggregation.sort(Sort.Direction.ASC, "workDate"),

                // ????????????
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)
        );

        /*
         * ????????????
         *   - aggregate(Aggregation aggregation, Class<?> inputType, Class<O> outputType)
         *       - aggregation: Aggregation ??????
         *       TODO: ??????
         *       - inputType:
         *       - outputType
         * */
        List<BookingScheduleRuleVo> bookingScheduleRuleVos = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class)
                .getMappedResults()
                .stream().peek(bookingScheduleRuleVo -> {
                    // ?????? dayOfWeek(?????????) ??????
                    Week dayOfWeekEnum = DateUtil.dayOfWeekEnum(bookingScheduleRuleVo.getWorkDate());
                    bookingScheduleRuleVo.setDayOfWeek(this.dateToStringOfWeek(dayOfWeekEnum));
                }).collect(Collectors.toList());

        // ???????????? count
        Aggregation totalAggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        int total = mongoTemplate.aggregate(totalAggregation, Schedule.class, BookingScheduleRuleVo.class).getMappedResults().size();

        // ???????????????
        String hosname = hospitalService.getHosnameByHoscode(hoscode);


        return Dict.create()
                .set("bookingScheduleRules", bookingScheduleRuleVos)
                .set("total", total)
                .set("hosname", hosname);
    }

    /**
     * ???????????????????????????
     *
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return
     */
    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        return scheduleRepository.findAllByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, DateUtil.parse(workDate, "yyyy-MM-dd"));
    }

    @Override
    public Map<String, Object> getBookingSchedule(String hoscode, String depcode, Integer page, Integer limit) {
        // ??????????????????
        Hospital hospital = hospitalService.getHospitalByHoscode(hoscode);
        if (hospital == null) {
            throw new BaseServiceException(ResultCodeEnum.PARAM_ERROR);
        }
        // ????????????????????????
        BookingRule bookingRule = hospital.getBookingRule();
        // ??????????????????????????????????????????????????????
        IPage<Date> iPage = this.getPageWorkDateList(page, limit, bookingRule);
        List<Date> dataList = iPage.getRecords();

        // ??????????????????
        Criteria criteria = Criteria.where("hoscode").is(hoscode)
                .and("depcode").is(depcode)
                .and("workDate").in(dataList);
        // ??????????????????
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber")
        );
        // ??????
        List<BookingScheduleRuleVo> scheduleRuleVos = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class).getMappedResults();

        // ????????? workDate - data ??????????????? Map
        Map<Date, BookingScheduleRuleVo> dataMap = scheduleRuleVos.stream()
                .collect(Collectors.toMap(
                        BookingScheduleRuleVo::getWorkDate,
                        bookingScheduleRuleVo -> bookingScheduleRuleVo));

        // ???????????????????????????
        AtomicInteger idx = new AtomicInteger(0);
        List<BookingScheduleRuleVo> bookingScheduleRuleVos = dataList.stream().map(workDate -> {
            BookingScheduleRuleVo bookingScheduleRuleVo = dataMap.get(workDate);
            if (bookingScheduleRuleVo == null) {
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setDocCount(0);
                bookingScheduleRuleVo.setStatus(-1);
            }
            bookingScheduleRuleVo.setWorkDate(workDate);
            bookingScheduleRuleVo.setWorkDateMd(workDate);
            bookingScheduleRuleVo.setDayOfWeek(this.dateToStringOfWeek(DateUtil.dayOfWeekEnum(workDate)));

            // ?????????????????????????????????????????? '????????????'
            if (page == iPage.getPages() && idx.get() == dataList.size() - 1) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }

            // ????????????????????????????????????????????????
            DateTime stopTime = DateTime.of(
                    DateUtil.format(new Date(), "yyyy-MM-dd") + " " + bookingRule.getStopTime(),
                    "yyyy-MM-dd HH:mm");
            // ??????????????????????????????????????????
            if (page == 1 && idx.get() == 0 && stopTime.isBefore(new Date())) {
                bookingScheduleRuleVo.setStatus(-1);
            }
            idx.getAndIncrement();
            return bookingScheduleRuleVo;
        }).collect(Collectors.toList());

        // ????????????????????????
        Department department = departmentService.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        Map<String, Object> baseMap = new HashMap<String, Object>(){{
            put("hosname", hospital.getHosname());
            put("depname", department.getDepname());
            put("bigname", department.getBigname());
            put("workDateString", DateUtil.format(new Date(), "yyyy???MM???"));
            put("releaseTime", bookingRule.getReleaseTime());
            put("stopTime", bookingRule.getStopTime());
        }};

        return new HashMap<String, Object>(){{
            put("bookingScheduleList", bookingScheduleRuleVos);
            put("total", iPage.getTotal());
            put("baseMap", baseMap);
        }};
    }

    @Override
    public Schedule getScheduleById(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BaseServiceException(ResultCodeEnum.PARAM_ERROR));
        return this.packSchedule(schedule);
    }

    @Override
    public void updateStock(Schedule schedule) {
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
    }

    /**
     * ????????????????????????
     *
     * @param page
     * @param limit
     * @param bookingRule
     * @return
     */
    private IPage<Date> getPageWorkDateList(Integer page, Integer limit, BookingRule bookingRule) {
        // ??? HH:ss -> yyyy-MM-dd HH:ss
        DateTime releaseTime = DateTime.of(DateUtil.format(new Date(), "yyyy-MM-dd") + " " + bookingRule.getReleaseTime(), "yyyy-MM-dd HH:mm");
        // ?????????????????????????????????????????????????????????????????? + 1
        boolean isBefore = releaseTime.isBefore(new Date());
        Integer cycle = bookingRule.getCycle();
        if (isBefore) {
            cycle = cycle + 1;
        }
        List<Date> dataList = new ArrayList<>();
        // ??????????????????????????????????????????
        for (int i = 0; i < cycle; i++) {
            DateTime futureTime = DateUtil.offsetDay(releaseTime, i);
            futureTime = DateUtil.parse(futureTime.toString("yyyy-MM-dd"));
            dataList.add(futureTime);
        }
        // ????????????
        int start = (page - 1) * limit;
        int end = start + limit;
        if (end > dataList.size()) {
            end = dataList.size();
        }
        dataList = dataList.subList(start, end);
        IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, limit, dataList.size());
        iPage.setRecords(dataList);
        return iPage;
    }


    /**
     * ?????? Week ?????????????????????????????????????????????
     *
     * @param week
     * @return
     */
    private String dateToStringOfWeek(Week week) {
        switch (week) {
            case SUNDAY:
                return "?????????";
            case MONDAY:
                return "?????????";
            case TUESDAY:
                return "?????????";
            case WEDNESDAY:
                return "?????????";
            case THURSDAY:
                return "?????????";
            case FRIDAY:
                return "?????????";
            case SATURDAY:
                return "?????????";
            default:
                throw new BaseServiceException(ResultCodeEnum.FAIL);
        }
    }

    private Schedule packSchedule(Schedule schedule) {
        Map<String, Object> params = schedule.getParams();
        params.put("hosname", hospitalService.getHosnameByHoscode(schedule.getHoscode()));
        Department department = departmentService.getDepartmentByHoscodeAndDepcode(schedule.getHoscode(), schedule.getDepcode());
        params.put("depname", department.getDepname());
        params.put("dayOfWeek", this.dateToStringOfWeek(DateUtil.dayOfWeekEnum(schedule.getWorkDate())));
        return schedule;
    }
}
