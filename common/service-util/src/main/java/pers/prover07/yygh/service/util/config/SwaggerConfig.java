package pers.prover07.yygh.service.util.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Classname SwaggerConfig
 * @Description Swagger 配置类
 * @Date 2021/11/18 15:31
 * @Created by Prover07
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * 配置 web api swagger docs
     * @return
     */
    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                // 配置分组名
                .groupName("web-api")
                // 配置文档信息
                .apiInfo(webApiInfo())
                .select()
                // 配置扫描的接口路径
                .paths(Predicates.and(PathSelectors.ant("/api/**")))
                .build();
    }

    /**
     * 配置 web api docs info
     * @return
     */
    private ApiInfo webApiInfo() {
        return new ApiInfoBuilder()
                .title("GHYY-API文档")
                .description("尚医通-API接口文档定义")
                .version("1.0")
                .contact(new Contact("Prover07", "https://github.com/Dreamer-07", "2391105059@qq.com"))
                .build();
    }

    /**
     * 配置 admin api swagger api
     * @return
     */
    @Bean
    public Docket adminApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("admin-api")
                .apiInfo(adminApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.ant("/admin/**")))
                .build();
    }

    /**
     * 配置 admin api docs info
     * @return
     */
    private ApiInfo adminApiInfo() {
        return new ApiInfoBuilder()
                .title("GHYY-ADMIN文档")
                .description("尚医通-后台管理接口文档")
                .version("1.0")
                .contact(new Contact("Prover07", "https://github.com/Dreamer-07", "2391105059@qq.com"))
                .build();
    }

}
