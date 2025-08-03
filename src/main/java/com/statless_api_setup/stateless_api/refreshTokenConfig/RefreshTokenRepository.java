package com.statless_api_setup.stateless_api.refreshTokenConfig;

import com.statless_api_setup.stateless_api.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    // Find a refresh token by its selector and check if it's not revoked
    Optional<RefreshTokenEntity> findBySelectorAndRevokedFalse(String selector);

    // Optional: Method to revoke or delete all refresh tokens for a user
    void deleteByUser(UserEntity user); // Revokes all refresh tokens associated with this user
}