package pers.prover07.yygh.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author by Prover07
 * @classname OrderServiceApplication
 * @description TODO
 * @date 2021/12/9 11:29
 */
@SpringBootApplication(scanBasePackages = "pers.prover07.yygh")
@EnableDiscoveryClient
@EnableFeignClients
@EnableSwagger2
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
