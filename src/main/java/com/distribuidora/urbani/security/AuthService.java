package com.distribuidora.urbani.security;

import com.distribuidora.urbani.entity.User;
import com.distribuidora.urbani.repository.UserRepository;
import com.distribuidora.urbani.security.dto.AuthResponse;
import com.distribuidora.urbani.security.dto.LoginRequest;
import com.distribuidora.urbani.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

/*
    public AuthResponse register(RegisterRequest registerRequest) {

        UserEntity user = userServiceImpl.register(registerRequest);
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        String accessToken = jwtService.generateAccessToken(customUserDetails);
        String refreshToken = jwtService.generateRefreshToken(customUserDetails);

        Instant now = Instant.now();
        user.setRefreshTokenJti(jwtService.extractJti(refreshToken));
        user.setRefreshTokenExpirationAt(now.plus(jwtService.getRefreshTokenExpirationInMs(), ChronoUnit.MILLIS));
        userRepository.save(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                SecurityConstants.TOKEN_TYPE_BEARER,
                now.plus(jwtService.getTokenExpirationInMs(), ChronoUnit.MILLIS),
                user.getRefreshTokenExpirationAt(),
                UserWebMapper.toResponse(user));
    }
*/

    public ResponseEntity<AuthResponse> login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password())
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Instant now = Instant.now();
        assert user != null;
        user.setRefreshTokenJti(jwtService.extractJti(refreshToken));
        user.setRefreshTokenExpirationAt(now.plus(jwtService.getRefreshTokenExpirationInMs(), ChronoUnit.MILLIS));
        userRepository.save(user);

        AuthResponse authResponse = new AuthResponse(
                accessToken,
                refreshToken,
                SecurityConstants.TOKEN_TYPE_BEARER,
                now.plus(jwtService.getTokenExpirationInMs(), ChronoUnit.MILLIS),
                user.getRefreshTokenExpirationAt());

        return ResponseEntity.ok(authResponse);
    }

/*
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

        String refreshToken = refreshTokenRequest.refreshToken();
        final String username = jwtService.extractUsername(refreshToken);
        String jti = jwtService.extractJti(refreshToken);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("username", username));

        if (user.getRefreshTokenExpirationAt() == null || user.getRefreshTokenExpirationAt().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException();
        }

        // validar refresh token
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        if (!jti.equals(user.getRefreshTokenJti())) {
            throw new InvalidRefreshTokenException();
        }


        // 4) Generar nuevo access token
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        String newAccessToken = jwtService.generateAccessToken(customUserDetails);

        // 5)
        Instant now = Instant.now();
        String newRefreshToken = jwtService.generateRefreshToken(customUserDetails);
        user.setRefreshTokenJti(jwtService.extractJti(newRefreshToken));
        user.setRefreshTokenExpirationAt(now.plus(jwtService.getRefreshTokenExpirationInMs(), ChronoUnit.MILLIS));
        userRepository.save(user);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                SecurityConstants.TOKEN_TYPE_BEARER,
                now.plus(jwtService.getTokenExpirationInMs(), ChronoUnit.MILLIS),
                user.getRefreshTokenExpirationAt(),
                UserWebMapper.toResponse(user)
        );
    }
*/
}
