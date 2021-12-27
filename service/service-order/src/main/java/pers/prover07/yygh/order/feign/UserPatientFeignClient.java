package pers.prover07.yygh.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pers.prover07.yygh.model.user.Patient;

/**
 * @author by Prover07
 * @classname UserPatientFeignClient
 * @description TODO
 * @date 2021/12/9 22:56
 */
@FeignClient("service-user")
@Service
public interface UserPatientFeignClient {

    @GetMapping("/api/user/patient/inner/info/{id}")
    public Patient getInfoByIdInner(@PathVariable("id") String patientId);

}
