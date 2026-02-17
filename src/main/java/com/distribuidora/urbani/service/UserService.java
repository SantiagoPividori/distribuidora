package com.distribuidora.urbani.service;

import com.distribuidora.urbani.entity.User;
import com.distribuidora.urbani.entity.utility.Role;
import com.distribuidora.urbani.exceptions.PasswordMismatchException;
import com.distribuidora.urbani.repository.UserRepository;
import com.distribuidora.urbani.security.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

//Implementamos UserDetailsService para poder autenticar el User

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest registerRequest) {

        if (!(registerRequest.confirmPassword().equals(registerRequest.password()))) {
            throw new PasswordMismatchException("Passwords don't match");
        }

        return new User(
                UUID.randomUUID(),
                registerRequest.firstName(),
                registerRequest.lastName(),
                generateUsername(registerRequest.firstName(), registerRequest.lastName()),
                passwordEncoder.encode(registerRequest.password()),
                Role.PREVENTISTA,
                true,
                null,
                null,
                true,
                true,
                true,
                true
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));

        if (user.getRole() == null) {
            throw new IllegalStateException("User has no roles: " + username);
        }

        return User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .role(user.getRole())
                .build();

    }

    private String generateUsername(String firstName, String lastName) {

        String baseUsername = (firstName + "." + lastName).toLowerCase()
                .replaceAll("[áàäâ]", "a")
                .replaceAll("[éèëê]", "e")
                .replaceAll("[íìïî]", "i")
                .replaceAll("[óòöô]", "o")
                .replaceAll("[úùüû]", "u")
                .replaceAll("ñ", "n")
                .replaceAll("[^a-z0-9.]", "");

        int count = 1;
        String username = baseUsername;

        while (userRepository.findByUsername(username).isPresent()) {
            username = baseUsername + count;
            count++;
        }
        return username;
    }

}