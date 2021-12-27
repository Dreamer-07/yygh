package pers.prover07.yygh.hosp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Classname ServiceHospApplication
 * @Description 医院服务模块
 * @Date 2021/11/18 14:21
 * @Created by Prover07
 */
@SpringBootApplication(scanBasePackages = "pers.prover07.yygh")
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceHospApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);
    }

}
