package pers.prover07.yygh.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * @Classname CosrConfig
 * @Description 配置跨域请求
 * @Date 2021/11/30 14:31
 * @Created by Prover07
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter(){
        CorsConfiguration config = new CorsConfiguration();
        // 请求方式 - 请求头 - 请求来源
        config.addAllowedMethod("*");
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");

        // 请求路径
        UrlBasedCorsConfigurationSource urlCorsConfig = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        urlCorsConfig.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(urlCorsConfig);


    }

}
