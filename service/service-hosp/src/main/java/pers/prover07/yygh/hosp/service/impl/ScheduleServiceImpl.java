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
        // 构建分页对象
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        // 构建模糊查询匹配规则
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        // 构建条件查询
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        Example<Schedule> scheduleExample = Example.of(schedule, exampleMatcher);

        return scheduleRepository.findAll(scheduleExample, pageable);
    }

    @Override
    public void saveSchedule(Map<String, Object> dataMap) {
        // 转换成实体类
        Schedule schedule = JSON.parseObject(JSON.toJSONString(dataMap), Schedule.class);

        // 判断是否存在
        Schedule existsSchedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());

        if (existsSchedule != null) {
            // 已经存在 - 为更新
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
        // 查找对应的实体类
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);

        if (schedule != null) {
            scheduleRepository.deleteById(schedule.getId());
            return;
        }

        throw new BaseServiceException(ResultCodeEnum.FAIL);
    }

    @Override
    public Map<String, Object> getRuleSchedule(String hoscode, String depcode, long page, long limit) {
        // 构建查询条件
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        // 构建复杂查询对象
        Aggregation aggregation = Aggregation.newAggregation(
                // 设置匹配条件
                Aggregation.match(criteria),

                // 设置分组信息, 根据 workDate 字段进行分组
                Aggregation.group("workDate")
                        // 设置查询出来后的列名
                        .first("workDate").as("workDate")
                        // 统计当天一个有多少个挂号源
                        .count().as("docCount")
                        // 统计总剩余挂号数和总已挂号数
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),

                // 排序(排序方式 + 排序字段)
                Aggregation.sort(Sort.Direction.ASC, "workDate"),

                // 设置分页
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)
        );

        /*
         * 获取数据
         *   - aggregate(Aggregation aggregation, Class<?> inputType, Class<O> outputType)
         *       - aggregation: Aggregation 规范
         *       TODO: 类型
         *       - inputType:
         *       - outputType
         * */
        List<BookingScheduleRuleVo> bookingScheduleRuleVos = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class)
                .getMappedResults()
                .stream().peek(bookingScheduleRuleVo -> {
                    // 设置 dayOfWeek(星期几) 属性
                    Week dayOfWeekEnum = DateUtil.dayOfWeekEnum(bookingScheduleRuleVo.getWorkDate());
                    bookingScheduleRuleVo.setDayOfWeek(this.dateToStringOfWeek(dayOfWeekEnum));
                }).collect(Collectors.toList());

        // 获取数据 count
        Aggregation totalAggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        int total = mongoTemplate.aggregate(totalAggregation, Schedule.class, BookingScheduleRuleVo.class).getMappedResults().size();

        // 获取医院名
        String hosname = hospitalService.getHosnameByHoscode(hoscode);


        return Dict.create()
                .set("bookingScheduleRules", bookingScheduleRuleVos)
                .set("total", total)
                .set("hosname", hosname);
    }

    /**
     * 获取详细的排班信息
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
        // 获取医院信息
        Hospital hospital = hospitalService.getHospitalByHoscode(hoscode);
        if (hospital == null) {
            throw new BaseServiceException(ResultCodeEnum.PARAM_ERROR);
        }
        // 获取医院排班规则
        BookingRule bookingRule = hospital.getBookingRule();
        // 根据医院排班规矩，获取可以预约的时间
        IPage<Date> iPage = this.getPageWorkDateList(page, limit, bookingRule);
        List<Date> dataList = iPage.getRecords();

        // 构建查询条件
        Criteria criteria = Criteria.where("hoscode").is(hoscode)
                .and("depcode").is(depcode)
                .and("workDate").in(dataList);
        // 构建聚合查询
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber")
        );
        // 查询
        List<BookingScheduleRuleVo> scheduleRuleVos = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class).getMappedResults();

        // 转换为 workDate - data 数据格式的 Map
        Map<Date, BookingScheduleRuleVo> dataMap = scheduleRuleVos.stream()
                .collect(Collectors.toMap(
                        BookingScheduleRuleVo::getWorkDate,
                        bookingScheduleRuleVo -> bookingScheduleRuleVo));

        // 对排班数据进行优化
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

            // 设置最后一页的最后一条状态为 '即将预约'
            if (page == iPage.getPages() && idx.get() == dataList.size() - 1) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }

            // 如果放号时间已经过了，就不能预约
            DateTime stopTime = DateTime.of(
                    DateUtil.format(new Date(), "yyyy-MM-dd") + " " + bookingRule.getStopTime(),
                    "yyyy-MM-dd HH:mm");
            // 当前时间是否超过当天放号时间
            if (page == 1 && idx.get() == 0 && stopTime.isBefore(new Date())) {
                bookingScheduleRuleVo.setStatus(-1);
            }
            idx.getAndIncrement();
            return bookingScheduleRuleVo;
        }).collect(Collectors.toList());

        // 封装基本信息数据
        Department department = departmentService.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        Map<String, Object> baseMap = new HashMap<String, Object>(){{
            put("hosname", hospital.getHosname());
            put("depname", department.getDepname());
            put("bigname", department.getBigname());
            put("workDateString", DateUtil.format(new Date(), "yyyy年MM月"));
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
     * 获取可预约的时间
     *
     * @param page
     * @param limit
     * @param bookingRule
     * @return
     */
    private IPage<Date> getPageWorkDateList(Integer page, Integer limit, BookingRule bookingRule) {
        // 将 HH:ss -> yyyy-MM-dd HH:ss
        DateTime releaseTime = DateTime.of(DateUtil.format(new Date(), "yyyy-MM-dd") + " " + bookingRule.getReleaseTime(), "yyyy-MM-dd HH:mm");
        // 判断放号时间是否已过，如果已过可预约天数应该 + 1
        boolean isBefore = releaseTime.isBefore(new Date());
        Integer cycle = bookingRule.getCycle();
        if (isBefore) {
            cycle = cycle + 1;
        }
        List<Date> dataList = new ArrayList<>();
        // 根据可预约天数，获取对应日期
        for (int i = 0; i < cycle; i++) {
            DateTime futureTime = DateUtil.offsetDay(releaseTime, i);
            futureTime = DateUtil.parse(futureTime.toString("yyyy-MM-dd"));
            dataList.add(futureTime);
        }
        // 设置分页
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
     * 根据 Week 判断周几后返回对应的字符串信息
     *
     * @param week
     * @return
     */
    private String dateToStringOfWeek(Week week) {
        switch (week) {
            case SUNDAY:
                return "星期日";
            case MONDAY:
                return "星期一";
            case TUESDAY:
                return "星期二";
            case WEDNESDAY:
                return "星期三";
            case THURSDAY:
                return "星期四";
            case FRIDAY:
                return "星期五";
            case SATURDAY:
                return "星期六";
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
