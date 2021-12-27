package pers.prover07.yygh.gateway.filter;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import pers.prover07.yygh.common.util.JwtUtil;
import pers.prover07.yygh.common.util.result.Result;
import pers.prover07.yygh.common.util.result.ResultCodeEnum;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author by Prover07
 * @Classname GlobalAuthFilter
 * @Description 全局认证拦截去
 * @Date 2021/12/2 13:37
 */
@Component
@Slf4j
public class GlobalAuthFilter implements GlobalFilter, Ordered {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 拦截方法
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求路径
        String path = exchange.getRequest().getURI().getPath();
        log.debug("Gateway-AuthFilter: {path: {}}", path);

        // 判断是否为内部系统请求
        if (antPathMatcher.match("/**/inner/**", path)) {
            return this.out(exchange.getResponse(), ResultCodeEnum.PERMISSION);
        }

        // 判断是否为 api 接口请求
        if (antPathMatcher.match("/**/api/**/auth/**", path)) {
            // 外部系统请求 - 判断用户是否登录
            String userId = this.getUserId(exchange.getRequest());
            if (StringUtils.isBlank(userId)) {
                return this.out(exchange.getResponse(), ResultCodeEnum.LOGIN_AUTH);
            }
        }
        return chain.filter(exchange);
    }

    /**
     * 获取令牌中的 userId 信息
     *
     * @param request
     * @return
     */
    private String getUserId(ServerHttpRequest request) {
        List<String> tokenList = request.getHeaders().get("token");
        String token = "";
        if (CollUtil.isNotEmpty(tokenList)) {
            token = tokenList.get(0);
        }
        if (!StringUtils.isBlank(token)) {
            return JwtUtil.getTokenInfo(token, "userId", String.class);
        }
        return "";
    }

    /**
     * 通过 response 返回响应信息
     *
     * @param response
     * @param resultCodeEnum
     * @return
     */
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
        Result<Object> result = Result.build(resultCodeEnum.getCode(), resultCodeEnum.getMessage());
        byte[] bits = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer dataBuffer = response.bufferFactory().wrap(bits);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(dataBuffer));
    }


    /**
     * 拦截器优先级
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
