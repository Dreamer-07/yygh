package pers.prover07.yygh.hosp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pers.prover07.yygh.model.hosp.Department;

/**
 * @Classname DepartmentRepository
 * @Description TODO
 * @Date 2021/11/25 14:06
 * @Created by Prover07
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
