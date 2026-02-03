package com.distribuidora.urbani.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
//Es un filtro que se ejecuta una vez por request, por eso extiende de esa clase
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailService;

    //Este metodo es el responsable de verificar que el token sea válido
    //request: el pedido que entra | response: lo que vamos a devolver | filterChain: pásale el control al siguiente filtro
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //Busca el valor del header Authorization
        final String authHeader = request.getHeader("Authorization");

        //Si no existe token o el header no empieza con Bearer devolvemos para que SpringSecurity se encargue y no nos metemos.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String username;
        //Extraemos el token del header sacando el Bearer ("Bearer ".length()=7)
        final String jwt = authHeader.substring(7);
        try {
            //Extraemos el username del token en un try catch por la probabilidad de reventar si no tenemos subject.
             username = jwtService.extractUsername(jwt);
        } catch (JwtException e) {
            filterChain.doFilter(request, response);
            return;
        }

        //Verificamos que hayamos leído el username y vemos que todavía no hay usuario autenticado para esta request
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            //Cargamos el usuario
            UserDetails userDetails = userDetailService.loadUserByUsername(username);


            if (jwtService.isAccessToken(jwt)) {
                //Creamos el objeto UsernamePasswordAuthenticationToken que sería la representación de usuario autenticado
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                //Agrega metadata de la request al token, por ejemplo: IP, session ID, etc.
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //Guardamos la autenticación en el SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        //Seguimos el filterChain
        filterChain.doFilter(request, response);
    }
}
