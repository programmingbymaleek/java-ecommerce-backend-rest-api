package com.statless_api_setup.stateless_api.refreshTokenConfig;

import com.statless_api_setup.stateless_api.user.UserEntity;
import jakarta.persistence.*;

import java.time.Instant;

public class RefreshTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private UserEntity user;

    @Column(nullable=false, length=200)
    private String tokenHash; // store a hash, never plaintext

    @Column(nullable=false)
    private Instant expiresAt;

    @Column(nullable=false)
    private boolean revoked = false;

    private Instant createdAt = Instant.now();

    // (optional) for reuse detection/rotation tracking
    private String replacedBy; // token id or a truncated hash

    // getters/setters
    public Long getId() { return id; }
    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
    public Instant getCreatedAt() { return createdAt; }
    public String getReplacedBy() { return replacedBy; }
    public void setReplacedBy(String replacedBy) { this.replacedBy = replacedBy; }

    public void setSelector(String selector) {

    }

    public void setVerifierHash(String verifierHash) {
    }

    public String getVerifierHash() {
        return "";
    }
}
