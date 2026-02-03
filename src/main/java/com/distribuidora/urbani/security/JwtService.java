package com.distribuidora.urbani.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

//TODO: #Poner la SECRET_KEY en una variable de entorno.

//Firma y valída tokens, también extrae la información.
@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;
    @Value("${security.jwt.expiration}")
    private long tokenExpirationInMs;
    @Value("${security.jwt.refresh.expiration}")
    private long refreshTokenExpirationInMs;

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_ROLE = "role";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    //Con este metodo transformamos el string a una key válida para utilizar
    private SecretKey getSigningKey() {
        //Pasamos él string a un array de bytes porque los algoritmos de firma trabajan con byte no con string
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        //Es un helper de JJWT que arma un objeto Key compatible con HMAC(HS256).
        //Valida, entre otras cosas, que la key tenga suficiente longitud para ser segura.
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String buildToken(UserDetails userDetails, long expirationMs, String tokenType) {

        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalStateException("User has no authorities"));

        var builder = Jwts.builder()
                //De quién es este token?
                .subject(userDetails.getUsername())
                //Claim es una parte del payload, que sería toda la información que mandamos, entonces esta es una parte de toda la información.
                .claim(CLAIM_TYPE, tokenType)
                //Cuando creamos él token
                .issuedAt(now)
                //Cuando expira el token
                .expiration(exp)
                //Firmado con: getSigningKey(), que sería nuestra secret key.
                .signWith(getSigningKey());
        //¿Resultado? Un string con toda la información.

        if (TYPE_REFRESH.equals(tokenType)) {
            String jti = UUID.randomUUID().toString();
            return builder.id(jti)
                    .compact();
        }

        return builder.claim(CLAIM_ROLE, role)
                .compact();
    }

    public String extractUsername(String token) {
        //Con esto leemos el sub del token. El sub sería el subject(de quien es el token)
        return extractAllClaims(token).getSubject();
    }

    private boolean isTokenValid(String token, String tokenType) {
        try {
            Claims claims = extractAllClaims(token);
            String type = (String) claims.get(CLAIM_TYPE);

            return tokenType.equals(type) && claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        return isTokenValid(token, TYPE_ACCESS);
    }

    public boolean isRefreshToken(String token) {
        return isTokenValid(token, TYPE_REFRESH);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractJti(String token) {
        return extractAllClaims(token).getId();
    }

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(userDetails, tokenExpirationInMs, TYPE_ACCESS);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, refreshTokenExpirationInMs, TYPE_REFRESH);
    }

    public Long getTokenExpirationInMs() {
        return tokenExpirationInMs;
    }

    public Long getRefreshTokenExpirationInMs() {
        return refreshTokenExpirationInMs;
    }
}