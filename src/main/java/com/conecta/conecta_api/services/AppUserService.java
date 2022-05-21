package com.conecta.conecta_api.services;

import com.conecta.conecta_api.data.AppUserRepository;
import com.conecta.conecta_api.data.RoleRepository;
import com.conecta.conecta_api.domain.AppUser;
import com.conecta.conecta_api.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AppUserService implements IAppUserService, UserDetailsService {
    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username);

        if (user == null) {
            log.error("Usuário {} não encontrado.", username);

            throw new UsernameNotFoundException(String.format("Usuário %s não encontrado.", username));
        }

        log.info("Usuário {} encontrado.", username);

        Collection<SimpleGrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public AppUser saveUser(AppUser user) {
        AppUser localUser = userRepository.findByUsername(user.getUsername());

        if (localUser != null) {
            log.warn("Username já cadastrado!");
            throw new IllegalStateException("Username já cadastrado!");
        }

        log.info("Salvando novo usuário {} no banco de dados.", user.getName());

        Role role = roleRepository.findByName("ROLE_USER");

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(role);

        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Salvando novo papel {} no banco de dados.", role.getName());

        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adicionando novo papel {} para o usuário {}.", roleName, username);

        AppUser user = userRepository.findByUsername(username);
        Role role = roleRepository.findByName(roleName);

        user.getRoles().add(role);
    }

    @Override
    public AppUser getUser(String username) {
        log.info("Buscando o usuário {}.", username);

        return userRepository.findByUsername(username);
    }

    @Override
    public List<AppUser> getUsers() {
        log.info("Buscando todos os usuários.");

        return userRepository.findAll();
    }
}
