package com.conecta.conecta_api.services.interfaces;

import com.conecta.conecta_api.domain.entities.AppUser;
import com.conecta.conecta_api.domain.entities.Role;

import java.util.List;
import java.util.Optional;

public interface IAppUserService {
    AppUser saveUser(AppUser user);

    Role saveRole(Role role);

    void addRoleToUser(String username, String roleName);

    Optional<AppUser> getUser(String username);

    Optional<AppUser> getUserById(Long userId);

    List<AppUser> getUsers();
}
