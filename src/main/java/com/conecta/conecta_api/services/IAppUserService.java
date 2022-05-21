package com.conecta.conecta_api.services;

import com.conecta.conecta_api.domain.AppUser;
import com.conecta.conecta_api.domain.Role;

import java.util.List;

public interface IAppUserService {
    AppUser saveUser(AppUser user);

    Role saveRole(Role role);

    void addRoleToUser(String username, String roleName);

    AppUser getUser(String username);

    List<AppUser> getUsers();
}
