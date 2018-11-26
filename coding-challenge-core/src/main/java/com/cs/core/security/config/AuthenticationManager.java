package com.cs.core.security.config;

import com.cs.core.security.impl.JWTProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private JWTProvider jwtProvider;

    @Autowired
    public AuthenticationManager(JWTProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        try {
            var token = authentication.getCredentials().toString();
            if (jwtProvider.validateToken(token)) {
                var authorities = jwtProvider.getAuthorities(token);
                var auth = new UsernamePasswordAuthenticationToken(
                    jwtProvider.getUsername(token),
                    null,
                    authorities
                );
                return Mono.just(auth);
            }
        } catch (Exception e) {
            return Mono.empty();
        }
        return Mono.empty();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
