package com.statless_api_setup.stateless_api.JWTSecurityConfiguration;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private final JwtEncoder encoder;

    public JwtService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String issueAccessToken(String subject, Long userId,
                                   Collection<? extends GrantedAuthority> authorities,
                                   Duration ttl) {
        Instant now = Instant.now();

        // Extract ROLE_* from authorities
        List<String> roles = (authorities == null ? List.<String>of() :
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(a -> a.startsWith("ROLE_"))
                        .distinct()
                        .toList()
        );

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("Wima Global Enterprise")
                .audience(List.of("stateless-api"))
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .subject(subject)    // email
                .claim("uid", userId)
                .claim("roles", roles)
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}


