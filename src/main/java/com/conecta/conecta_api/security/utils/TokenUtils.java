package com.conecta.conecta_api.security.utils;

import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

public class TokenUtils {
    // TODO: 21/05/2022  atualizar secret jwt
    private final String secret = "secret";

    private final Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public Date getTokenExpiration() {
        return new Date(System.currentTimeMillis() + 60 * 60 * 1000); // 60 min
    }

    public Date getRefreshTokenExpiration() {
        return new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000); // 24h
    }
}
