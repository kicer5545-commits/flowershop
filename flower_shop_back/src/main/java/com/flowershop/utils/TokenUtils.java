package com.flowershop.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.flowershop.model.User;

import java.util.Date;

public class TokenUtils {
    private static final String SECRET = "flower_shop_secret_key_2026";
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    public static String createToken(User user) {
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return JWT.create()
                .withClaim("id", user.getId())
                .withClaim("userName", user.getUserName())
                .withClaim("role", user.getRole())
                .withExpiresAt(expireDate)
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static boolean verifyToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}