package pers.prover07.yygh.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Classname ServiceUserApplication
 * @Description TODO
 * @Date 2021/12/1 14:07
 * @author  by Prover07
 */
@SpringBootApplication(scanBasePackages = "pers.prover07.yygh")
@EnableSwagger2
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }

}
