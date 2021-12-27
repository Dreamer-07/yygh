package pers.prover07.yygh.hosp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pers.prover07.yygh.model.hosp.Schedule;

import java.util.Date;
import java.util.List;

/**
 * @Classname ScheduleRepository
 * @Description TODO
 * @Date 2021/11/25 19:16
 * @Created by Prover07
 */
@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {

    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    List<Schedule> findAllByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date workDate);
}
