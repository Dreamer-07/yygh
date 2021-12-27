package pers.prover07.yygh.hosp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pers.prover07.yygh.model.hosp.Hospital;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Classname HospitalRepository
 * @Description TODO
 * @Date 2021/11/24 19:47
 * @Created by Prover07
 */
@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {
    Hospital getHospitalByHoscode(String hoscode);

    List<Hospital> getAllByHosnameLike(String hosname);
}
