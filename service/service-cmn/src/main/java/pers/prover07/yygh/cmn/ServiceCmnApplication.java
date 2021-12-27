package pers.prover07.yygh.cmn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Classname ServiceCmnApplication
 * @Description TODO
 * @Date 2021/11/22 19:17
 * @Created by Prover07
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "pers.prover07.yygh")
public class ServiceCmnApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceCmnApplication.class, args);
    }

}
