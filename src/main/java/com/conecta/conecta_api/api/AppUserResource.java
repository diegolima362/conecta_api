package com.conecta.conecta_api.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.conecta.conecta_api.domain.dtos.AppUserInfo;
import com.conecta.conecta_api.domain.entities.AppUser;
import com.conecta.conecta_api.domain.entities.Role;
import com.conecta.conecta_api.security.utils.TokenUtils;
import com.conecta.conecta_api.services.AppUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class AppUserResource {
    private final AppUserService userService;
    private final TokenUtils jwtUtils;

    @PostMapping(path = "/register")
    public ResponseEntity<AppUserInfo> saveUser(@RequestBody AppUser user) {
        AppUser savedUser = userService.saveUser(user);
        AppUserInfo userInfo = AppUserInfo.fromAppUser(savedUser);

        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/users/register").toUriString());

        return ResponseEntity.created(uri).body(userInfo);
    }

    @GetMapping(path = "/users")
    public ResponseEntity<List<AppUserInfo>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers().stream().map(AppUserInfo::fromAppUser).collect(Collectors.toList()));
    }

    @GetMapping(path = "/users/me")
    public ResponseEntity<AppUserInfo> getUserInfo(HttpServletRequest request) {
        TokenInfo info = extractInfo(request.getHeader(AUTHORIZATION));
        var user = userService.getUser(info.username());

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(AppUserInfo.fromAppUser(user.get()));
    }

    @PostMapping(path = "/role/save")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/role/save").toUriString());

        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping(path = "/role/addtouser")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form) {
        userService.addRoleToUser(form.getUsername(), form.getRolename());

        return ResponseEntity.ok().build();
    }


    @GetMapping(path = "/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String authorizationHeader = request.getHeader(AUTHORIZATION);

            TokenInfo token = extractInfo(authorizationHeader);
            var user = userService.getUser(token.username());

            if (user.isEmpty()) {
                throw new NoSuchElementException();
            }

            String accessToken = JWT.create()
                    .withSubject(user.get().getUsername())
                    .withExpiresAt(jwtUtils.getTokenExpiration())
                    .withIssuer(request.getRequestURL().toString())
                    .withClaim("roles", user.get()
                            .getRoles()
                            .stream()
                            .map(Role::getName)
                            .collect(Collectors.toList()))
                    .sign(jwtUtils.getAlgorithm());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", accessToken);
            tokens.put("refresh_token", token.refreshToken());

            response.setContentType(APPLICATION_JSON_VALUE);

            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        } catch (Exception exception) {
            response.setStatus(FORBIDDEN.value());

            Map<String, String> error = new HashMap<>();
            error.put("error_message", exception.getMessage());
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
    }

    static TokenInfo getTokenInfo(String authorizationHeader, TokenUtils jwtUtils) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String refreshToken = authorizationHeader.substring("Bearer ".length());
            Algorithm algorithm = jwtUtils.getAlgorithm();

            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(refreshToken);

            return new TokenInfo("", refreshToken, decodedJWT.getSubject());
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

    TokenInfo extractInfo(String authorizationHeader) {
        return getTokenInfo(authorizationHeader, jwtUtils);
    }
}

