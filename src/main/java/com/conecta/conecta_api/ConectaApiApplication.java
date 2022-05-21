package com.conecta.conecta_api;

import com.conecta.conecta_api.domain.AppUser;
import com.conecta.conecta_api.domain.Role;
import com.conecta.conecta_api.services.AppUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;

import static java.util.Calendar.*;


@SpringBootApplication
public class ConectaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConectaApiApplication.class, args);
    }

    @Bean
    CommandLineRunner run(AppUserService userService) {
        return args -> {
            userService.saveRole(new Role(null, "ROLE_USER"));
            userService.saveRole(new Role(null, "ROLE_MANAGER"));
            userService.saveRole(new Role(null, "ROLE_ADMIN"));
            userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

            userService.saveUser(new AppUser(null, "Diego Lima", LocalDate.of(1999, JUNE, 24), "diegolima@gmail.com", "diegolima", "1234", new ArrayList<>()));
            userService.saveUser(new AppUser(null, "Renan Gustavo", LocalDate.of(2001, MAY, 15), "renangustavo@gmail.com", "r3n4n", "5678", new ArrayList<>()));
            userService.saveUser(new AppUser(null, "Bruno Diniz", LocalDate.of(1986, APRIL, 30), "brunodinz@gmail.com", "brdiniz", "9ABC", new ArrayList<>()));
            userService.saveUser(new AppUser(null, "João Ninguem", LocalDate.of(2003, FEBRUARY, 5), "jao@gmail.com", "jao", "DEF0", new ArrayList<>()));

            userService.addRoleToUser("diegolima", "ROLE_SUPER_ADMIN");
            userService.addRoleToUser("diegolima", "ROLE_ADMIN");
            userService.addRoleToUser("diegolima", "ROLE_MANAGER");
            userService.addRoleToUser("diegolima", "ROLE_USER");

            userService.addRoleToUser("r3n4n", "ROLE_ADMIN");
            userService.addRoleToUser("r3n4n", "ROLE_MANAGER");
            userService.addRoleToUser("r3n4n", "ROLE_USER");

            userService.addRoleToUser("brdiniz", "ROLE_MANAGER");
            userService.addRoleToUser("brdiniz", "ROLE_USER");

            userService.addRoleToUser("jao", "ROLE_USER");
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
