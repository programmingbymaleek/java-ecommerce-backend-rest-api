package com.statless_api_setup.stateless_api.JWTSecurityConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;
@Service
public class JwtService {
        private final JwtEncoder encoder;
        public JwtService(JwtEncoder encoder) { this.encoder = encoder; }
        public String issueAccessToken(String subject, Collection<? extends GrantedAuthority> authorities, Duration ttl) {
            Instant now = Instant.now();
            String scope = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("Wima Global Enterprise")
                    .issuedAt(now)
                    .expiresAt(now.plus(ttl))
                    .subject(subject)
                    .claim("scope", scope)
                    .build();
            //using the encoder now to generate a token now
            return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        }
    }


