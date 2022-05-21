package com.conecta.conecta_api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserInfo {
    private Long id;
    private String name;
    private String username;
    private String email;

    static public AppUserInfo fromAppUser(AppUser user) {
        return new AppUserInfo(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
