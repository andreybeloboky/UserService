package com.beloboki.config;

import java.util.Collection;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CustomJwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String username = jwt.getSubject();
        Long userId = jwt.getClaim("userId");
        String role = jwt.getClaim("role");

        CurrentUser currentUser = new CurrentUser(userId, username, role);
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new UsernamePasswordAuthenticationToken(
                currentUser, jwt.getTokenValue(), authorities);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        String role = jwt.getClaim("role");
        if (role == null) return List.of();

        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }
}
