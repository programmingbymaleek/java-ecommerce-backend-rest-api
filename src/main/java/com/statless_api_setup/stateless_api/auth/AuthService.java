package com.statless_api_setup.stateless_api.auth;

import com.statless_api_setup.stateless_api.JWTSecurityConfiguration.JwtService;
import com.statless_api_setup.stateless_api.refreshTokenConfig.RefreshTokenService;
import com.statless_api_setup.stateless_api.user.UserEntity;
import com.statless_api_setup.stateless_api.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AuthService {

    // DTOs (move to auth.dto if you prefer)
    public record LoginRequest(String email, String password) {}
    public record TokenResponse(String accessToken, String tokenType, long expiresInSeconds) {}

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authManager,
                       JwtService jwtService,
                       UserRepository userRepository,
                       RefreshTokenService refreshTokenService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * Authenticate, issue access token, create refresh token cookie.
     */
    public TokenResponse login(LoginRequest req, HttpServletResponse res) {
        // 1) Authenticate using email (matches your CustomUserDetailsService)
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        // 2) Load user by email (avoid relying on auth.getName() ambiguity)
        UserEntity user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3) Issue short-lived access token (e.g., 15 minutes)
        String accessToken = jwtService.issueAccessToken(
                user.getEmail(),
                auth.getAuthorities(),
                Duration.ofMinutes(15)
        );

        // 4) Create refresh token ("selector.verifier") and set as HttpOnly cookie
        String refreshCookieValue = refreshTokenService.createRefreshToken(user, Duration.ofDays(14));

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", refreshCookieValue)
                .httpOnly(true)
                .secure(false)            // set true in production (HTTPS)
                .sameSite("Lax")          // if cross-site, use "None" + secure(true)
                .path("/auth/refresh")    // only sent to refresh endpoint
                .maxAge(Duration.ofDays(14))
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        // 5) Return access token payload
        return new TokenResponse(accessToken, "Bearer", 900); // 900s = 15 min
    }
}
