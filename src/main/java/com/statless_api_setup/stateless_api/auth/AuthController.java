package com.statless_api_setup.stateless_api.auth;

import com.statless_api_setup.stateless_api.JWTSecurityConfiguration.JwtService;
import com.statless_api_setup.stateless_api.refreshTokenConfig.RefreshTokenService;
import com.statless_api_setup.stateless_api.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;



@RestController
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class); // Logger for debugging

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authManager,
                          JwtService jwtService,
                          RefreshTokenService refreshTokenService,
                          UserRepository userRepository) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    @GetMapping("/testAuth")
    public ResponseEntity<String> testAuth() {
        return ResponseEntity.ok("AuthController is working!");
    }

    // Record to capture login request
    public record LoginRequest(String username, String password) {
    }

    // Record to capture the response with Access Token
    public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletResponse res) {
        log.debug("Login attempt for user: {}", req.username());
        // Step 1: Authenticate the user using the provided username and password
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        // Step 2: Generate Access Token
        String accessToken = jwtService.issueAccessToken(
                auth.getName(),
                auth.getAuthorities(),
                Duration.ofMinutes(15) // e.g., 15-minute TTL for access token
        );

        // Step 3: Generate Refresh Token (this will be done with a separate service)
        var user = userRepository.findByEmail(auth.getName()).orElseThrow();
        String refreshToken = refreshTokenService.createRefreshToken(user, Duration.ofDays(14)); // Store for 14 days

        // Step 4: Send the Refresh Token as an HttpOnly cookie
        ResponseCookie cookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                .httpOnly(true)
                .secure(false) // Set to true for production (HTTPS)
                .sameSite("Lax") // Adjust this based on your actual needs
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(14)) // Set the expiration here
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Step 5: Return the Access Token in the response body
        return ResponseEntity.ok(new TokenResponse(accessToken, "Bearer", 900));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String username, HttpServletResponse res) {
        userRepository.findByEmail(username).ifPresent(user -> {
            refreshTokenService.revokeAllForUser(user.getId()); // Revokes all refresh tokens for the user
        });

        // Clear the refresh token cookie
        ResponseCookie clearCookie = ResponseCookie.from("REFRESH_TOKEN", "")
                .httpOnly(true)
                .secure(false) // Set to true for production (HTTPS)
                .sameSite("Lax") // Adjust for CSRF based on your needs
                .path("/auth/refresh")
                .maxAge(0) // This indicates the cookie should be deleted
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        return ResponseEntity.noContent().build(); // Return 204 No Content
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken,
                                     HttpServletResponse res) {
        // Check if the refresh token is present
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "missing_refresh_token"));
        }

        // Validate and rotate the refresh token using the RefreshTokenService
        try {
            var rotated = refreshTokenService.rotate(refreshToken, Duration.ofDays(14));

            // Generate new access token
            String accessToken = jwtService.issueAccessToken(
                    rotated.user().getEmail(),
                    rotated.user().getRoles().stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                            .toList(),
                    Duration.ofMinutes(15) // New short-lived access token
            );

            // Set new HttpOnly refresh token in the response
            ResponseCookie cookie = refreshTokenService.buildRefreshCookie(rotated.newCookieValue(), Duration.ofDays(14), false);
            res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(new TokenResponse(accessToken, "Bearer", 900)); // Send the new access token
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        }
    }
}