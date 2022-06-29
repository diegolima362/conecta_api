package com.conecta.conecta_api;

import com.conecta.conecta_api.security.utils.TokenUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
public class ConectaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConectaApiApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    TokenUtils tokenUtils() {
        return new TokenUtils();
    }
}
