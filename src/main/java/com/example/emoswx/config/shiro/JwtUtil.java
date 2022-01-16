package com.example.emoswx.config.shiro;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.signers.AlgorithmUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.emoswx.exception.EmosException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author ZZy
 * @date 2022/1/9 16:31
 * @description
 */


/**
 * 和令牌相关的操作
 * 创建令牌、检查令牌的合法性、通过令牌查询userId
 * */
@Component
public class JwtUtil {

    @Value("${emos.jwt.secret}")
    private String secret;

    @Value("${emos.jwt.expire}")
    private int expire;

    public String creatToken(int userId) {
        //加密对象
        Algorithm algorithm = Algorithm.HMAC256(secret);
        //过期日期
        Date date = DateUtil.offset(new Date(), DateField.DAY_OF_YEAR, expire);
        //计算token
        JWTCreator.Builder builder = JWT.create();
        String token = builder.withClaim("userId", userId).withExpiresAt(date).sign(algorithm);
        return token;
    }

    /**
     * 解析token，获取用户id
     * */
    public int getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asInt();
        } catch (Exception e) {
            throw new EmosException("令牌无效");
        }

    }

    /**
     * 验证token的合法性
     * */
    public void verifyToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        //TODO 没有验证时间，仅仅验证token是否合法？
        verifier.verify(token);
    }
}
