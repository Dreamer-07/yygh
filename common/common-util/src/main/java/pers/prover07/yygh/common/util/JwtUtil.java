package pers.prover07.yygh.common.util;

import cn.hutool.core.util.IdUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * @author Prover07
 * @Classname JwtUtil
 * @Description jwt 工具类
 * @Date 2021/12/1 16:02
 */
public class JwtUtil {

    /**
     * 定义过期时间
     */
    private static final long EXPIRED_TIME = 60 * 60 * 24 * 1000;

    /**
     * 定义密钥
     */
    private static final String SIGN_KEY = "byqtxdy";

    /**
     * 根据 userId & username 生成 jwt token
     * @param userId
     * @param username
     * @return
     */
    public static String createJwtToken(String userId, String username){
        return Jwts.builder()
                .setId(IdUtil.fastSimpleUUID())
                .setSubject("yygh-user")
                .setIssuedAt(new Date())
                .claim("userId", userId)
                .claim("username", username)
                .signWith(SignatureAlgorithm.HS512, SIGN_KEY)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRED_TIME))
                .compact();
    }

    /**
     * 获取 JWT 中的信息
     * @param jwtToken
     * @param key
     * @param clazz
     * @return
     */
    public static <T> T getTokenInfo(String jwtToken, String key, Class<T> clazz){
        return Jwts.parser()
                .setSigningKey(SIGN_KEY)
                .parseClaimsJws(jwtToken)
                .getBody()
                .get(key, clazz);
    }

}
