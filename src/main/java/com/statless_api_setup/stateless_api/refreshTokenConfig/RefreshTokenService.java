package com.statless_api_setup.stateless_api.refreshTokenConfig;

import com.statless_api_setup.stateless_api.user.UserEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository repository; // Injected repository
    private final PasswordEncoder passwordEncoder; // Password encoder for hashing

    // SecureRandom and Base64 encoder for generating tokens
    private static final SecureRandom RNG = new SecureRandom();
    private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();

    public RefreshTokenService(RefreshTokenRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates and stores a new refresh token for a given user.
     * Returns a string formatted as "selector.verifierPlain".
     */
    public String createRefreshToken(UserEntity user, Duration ttl) {
        // Generate two parts of the token
        String selector = randomToken(16);         // Secure random string for the selector
        String verifierPlain = randomToken(32);    // Secure random string for the verifier

        // Hash the verifier
        String verifierHash = passwordEncoder.encode(verifierPlain);

        // Create a new RefreshTokenEntity
        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setUser(user);
        token.setSelector(selector);
        token.setVerifierHash(verifierHash);
        token.setExpiresAt(Instant.now().plus(ttl)); // Set expiry time
        token.setRevoked(false);

        // Save to database
        repository.save(token);

        // Return the formatted token to the client
        return selector + "." + verifierPlain; // Send this to the client
    }

    /**
     * Validates the given refresh token and returns the matching RefreshTokenEntity.
     */
    public Optional<RefreshTokenEntity> validateRefreshToken(String token) {
        // Split the incoming token into selector and verifierPlain
        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            return Optional.empty(); // Invalid format
        }

        String selector = parts[0];         // Extract selector
        String verifierPlain = parts[1];    // Extract verifierPlain

        // Look up the token by selector
        Optional<RefreshTokenEntity> storedToken = repository.findBySelectorAndRevokedFalse(selector);
        if (storedToken.isEmpty()) return Optional.empty();

        RefreshTokenEntity entity = storedToken.get();

        // Check that the token hasn't expired
        if (entity.isRevoked() || entity.getExpiresAt().isBefore(Instant.now())) {
            return Optional.empty(); // Expired or revoked token
        }

        // Verify the entered verifier against the stored verifierHash
        if (!passwordEncoder.matches(verifierPlain, entity.getVerifierHash())) {
            return Optional.empty(); // Token verification failed
        }

        return Optional.of(entity); // Valid token
    }

    /**
     * Revokes a given refresh token.
     */
    public void revokeToken(RefreshTokenEntity token) {
        token.setRevoked(true); // Set the revoked flag
        repository.save(token); // Save the updated entity
    }


    public void revokeAllForUser(Long userId) {
        // Get all refresh tokens for the user and revoke them
        List<RefreshTokenEntity> tokens = repository.findAll().stream()
                .filter(token -> token.getUser().getId().equals(userId))
                .toList();

        for (RefreshTokenEntity token : tokens) {
            token.setRevoked(true); // Mark the token as revoked
            repository.save(token); // Save the updated entity
        }
    }

    /** Generate secure random tokens */
    private static String randomToken(int length) {
        byte[] bytes = new byte[length];
        RNG.nextBytes(bytes); // Generate random bytes
        return B64.encodeToString(bytes); // Encode to Base64 URL
    }


    public RotationResult rotate(String cookieValue, Duration ttl) {
        // Split the cookie value into selector and verifierPlain
        String[] parts = cookieValue.split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid refresh token format");
        }

        String selector = parts[0];         // Extract selector part
        String verifierPlain = parts[1];    // Extract verifierPlain part

        // Find the token in the database by selector
        Optional<RefreshTokenEntity> storedTokenOpt = repository.findBySelectorAndRevokedFalse(selector);

        if (storedTokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        RefreshTokenEntity storedToken = storedTokenOpt.get();

        // Check if the token has expired or is revoked
        if (storedToken.isRevoked() || storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Expired or revoked refresh token");
        }

        // Verify the verifierPlain against the stored hash
        if (!passwordEncoder.matches(verifierPlain, storedToken.getVerifierHash())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // Create a new selection and verifier
        String newSelector = randomToken(16);
        String newVerifierPlain = randomToken(32);
        String newVerifierHash = passwordEncoder.encode(newVerifierPlain); // Hash new verifier

        // Create new token entity for the refreshed token
        RefreshTokenEntity newToken = new RefreshTokenEntity();
        newToken.setUser(storedToken.getUser());
        newToken.setSelector(newSelector);
        newToken.setVerifierHash(newVerifierHash);
        newToken.setExpiresAt(Instant.now().plus(ttl)); // Set new expiry time
        newToken.setRevoked(false);

        // Save the new token to the repository
        repository.save(newToken);

        // Mark the old token as revoked
        storedToken.setRevoked(true);
        repository.save(storedToken);

        // Return the new token as a formatted string
        String newCookieValue = newSelector + "." + newVerifierPlain;
        return new RotationResult(storedToken.getUser(), newCookieValue); // Return the user and new cookie value
    }

    public ResponseCookie buildRefreshCookie(String value, Duration ttl, boolean secure) {
        return ResponseCookie.from("REFRESH_TOKEN", value)
                .httpOnly(true) // Ensures cookie is not accessible via JavaScript
                .secure(secure) // Set to true in production
                .sameSite("Lax") // Adjust based on your needs
                .path("/auth/refresh") // Path for which the cookie is valid
                .maxAge(ttl) // Validity duration
                .build();
    }


}