package com.dormnet.commonsecurity.helper;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt token) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                        jwtGrantedAuthoritiesConverter.convert(token).stream(),
                        getRealmRoles(token).stream())
                .collect(Collectors.toSet());
        return new JwtAuthenticationToken(token, authorities, token.getClaim("preferred_username"));
    }

    private Collection<? extends GrantedAuthority> getRealmRoles(Jwt token) {
        return Optional.ofNullable(token.getClaim("realm_access"))
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(realmAccess -> (Collection<String>) realmAccess.get("roles"))
                .map(roles -> roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

}
