package com.statless_api_setup.stateless_api.refreshTokenConfig;

import com.statless_api_setup.stateless_api.user.UserEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final PasswordEncoder encoder;

    private static final SecureRandom RNG = new SecureRandom();
    private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();

    // Cookie settings (adjust for prod)
    private static final String COOKIE_NAME = "REFRESH_TOKEN";
    private static final String COOKIE_PATH = "/auth/refresh";

    public RefreshTokenService(RefreshTokenRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    /** Mint a new refresh token for a user; returns the cookie-ready string "selector.verifier" */
    public String mint(UserEntity user, Duration ttl) {
        String selector = randomUrlToken(16); // 16 bytes → ~22 chars
        String verifierPlain = randomUrlToken(64); // 64 bytes → ~86 chars
        String verifierHash = encoder.encode(verifierPlain);

        var entity = new RefreshTokenEntity();
        entity.setUser(user);
        entity.setSelector(selector);
        entity.setVerifierHash(verifierHash);
        entity.setExpiresAt(Instant.now().plus(ttl));
        repo.save(entity);

        return selector + "." + verifierPlain;
    }

    /** Validate & rotate; returns new cookie string and the user */
    public RotationResult rotate(String cookieValue, Duration ttl) {
        var parts = cookieValue != null ? cookieValue.split("\\.", 2) : new String[0];
        if (parts.length != 2) throw new IllegalArgumentException("Invalid refresh token format");

        String selector = parts[0];
        String presentedVerifier = parts[1];

        var token = repo.findBySelectorAndRevokedFalse(selector)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            token.setRevoked(true);
            repo.save(token);
            throw new IllegalArgumentException("Refresh token expired");
        }

        if (!encoder.matches(presentedVerifier, token.getVerifierHash())) {
            // Optional: revoke all for this user for suspected theft
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // Rotate: revoke old token and create a new one
        token.setRevoked(true);

        String newSelector = randomUrlToken(16);
        String newVerifierPlain = randomUrlToken(64);
        String newVerifierHash = encoder.encode(newVerifierPlain);

        var replacement = new RefreshTokenEntity();
        replacement.setUser(token.getUser());
        replacement.setSelector(newSelector);
        replacement.setVerifierHash(newVerifierHash);
        replacement.setExpiresAt(Instant.now().plus(ttl));
        repo.save(replacement);

        token.setReplacedBy(replacement.getId().toString());
        repo.save(token);

        String newCookieValue = newSelector + "." + newVerifierPlain;
        return new RotationResult(token.getUser(), newCookieValue);
    }

    public void revokeAllForUser(Long userId) {
        repo.findAll().stream()
                .filter(t -> t.getUser().getId().equals(userId) && !t.isRevoked())
                .forEach(t -> { t.setRevoked(true); repo.save(t); });
    }

    // Build HttpOnly Set-Cookie header value
    public ResponseCookie buildRefreshCookie(String value, Duration ttl, boolean secureProd) {
        return ResponseCookie.from(COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secureProd)         // true in prod (HTTPS)
                .sameSite(secureProd ? "None" : "Lax") // if cross-site in prod, use None + Secure
                .path(COOKIE_PATH)
                .maxAge(ttl)
                .build();
    }

    public ResponseCookie clearRefreshCookie(boolean secureProd) {
        return ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secureProd)
                .sameSite(secureProd ? "None" : "Lax")
                .path(COOKIE_PATH)
                .maxAge(0)
                .build();
    }

    private static String randomUrlToken(int numBytes) {
        byte[] bytes = new byte[numBytes];
        RNG.nextBytes(bytes);
        return B64.encodeToString(bytes);
    }

    public record RotationResult(UserEntity user, String newCookieValue) {}
}