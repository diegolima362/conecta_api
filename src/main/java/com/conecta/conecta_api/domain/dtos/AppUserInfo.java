package com.conecta.conecta_api.domain.dtos;

import com.conecta.conecta_api.domain.entities.AppUser;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AppUserInfo implements Serializable {
    private Long id;
    private String name;
    private String username;
    private String email;
    private boolean admin;

    static public AppUserInfo fromAppUser(AppUser user) {
        return new AppUserInfo(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"))
        );
    }
}
