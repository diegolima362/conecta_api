package com.conecta.conecta_api.services;

import com.conecta.conecta_api.data.AppUserRepository;
import com.conecta.conecta_api.data.RoleRepository;
import com.conecta.conecta_api.domain.entities.AppUser;
import com.conecta.conecta_api.domain.entities.Role;
import com.conecta.conecta_api.services.interfaces.IAppUserService;
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
import java.util.Optional;
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
        Optional<AppUser> optionalAppUser = userRepository.findByUsername(username);

        if (optionalAppUser.isEmpty()) {
            log.error("Usuário {} não encontrado.", username);

            throw new UsernameNotFoundException(String.format("Usuário %s não encontrado.", username));
        }

        var user = optionalAppUser.get();

        log.info("Usuário {} encontrado.", username);

        Collection<SimpleGrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new User(
                user.getUsername(),
                user.getPassword(),
                authorities);
    }

    @Override
    public AppUser saveUser(AppUser user) {
        var userByUsername = userRepository.findByUsername(user.getUsername());

        var userByEmail = userRepository.findByEmail(user.getEmail());

        if (userByUsername.isPresent()) {
            log.warn("Username já cadastrado!");
            throw new IllegalStateException("Username já cadastrado!");
        } else if (userByEmail.isPresent()) {
            throw new IllegalStateException("Email já cadastrado!");
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
        var optionalAppUser = userRepository.findByUsername(username);

        if (optionalAppUser.isEmpty()) {
            log.error("Usuário {} não encontrado.", username);

            throw new UsernameNotFoundException(String.format("Usuário %s não encontrado.", username));
        }

        Role role = roleRepository.findByName(roleName);

        var user = optionalAppUser.get();

        if (user.getRoles().contains(role)) {
            log.info("Usuário {} já possui o papel {}.", username, roleName);
            return;
        }

        log.info("Adicionando novo papel {} para o usuário {}.", roleName, username);

        user.getRoles().add(role);
    }

    @Override
    public Optional<AppUser> getUser(String username) {
        log.info("Buscando o usuário {}.", username);

        return userRepository.findByUsername(username);

    }

    @Override
    public Optional<AppUser> getUserById(Long userId) {
        log.info("Buscando o usuário {}.", userId);

        return userRepository.findById(userId);
    }

    @Override
    public List<AppUser> getUsers() {
        log.info("Buscando todos os usuários.");

        return userRepository.findAll();
    }
}
