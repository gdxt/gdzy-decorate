package com.chungyu.miniapp.constants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * @author ywxd
 * @version V1.0
 * Jwt工具箱，包含生成Jwt与解析Jwt的方法
 */
public class JwtUtils {

    //生成token
    public static String generateToken(UserToken userToken, Long millisExpire) throws Exception {
        String token = Jwts.builder()
                .setSubject(userToken.getUsername())
                .claim(CommonConstants.CONTEXT_USER_ID, userToken.getUserId())
                .claim(CommonConstants.CONTEXT_NAME, userToken.getName())
                .claim(CommonConstants.RENEWAL_TIME, new Date(System.currentTimeMillis() + millisExpire / 2))
                .setExpiration(new Date(System.currentTimeMillis() + millisExpire))
                .signWith(SignatureAlgorithm.HS256, CommonConstants.JWT_PRIVATE_KEY)
                .compact();
        return token;
    }

    //解析token
    public static UserToken getInfoFromToken(String token) throws Exception {
        Claims claims = Jwts.parser()
                .setSigningKey(CommonConstants.JWT_PRIVATE_KEY).parseClaimsJws(token)
                .getBody();
        return new UserToken(claims.getSubject(), claims.get(CommonConstants.CONTEXT_USER_ID).toString(), claims.get(CommonConstants.CONTEXT_NAME).toString());
    }
}
