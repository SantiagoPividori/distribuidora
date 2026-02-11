package com.distribuidora.urbani.service;

import com.distribuidora.urbani.entity.User;
import com.distribuidora.urbani.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//Implementamos UserDetailsService para poder autenticar el User

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));

        if (user.getRole() == null) {
            throw new IllegalStateException("User has no roles: " + username);
        }

        return user;

    }
}