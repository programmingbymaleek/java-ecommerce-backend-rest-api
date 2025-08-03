package com.statless_api_setup.stateless_api.refreshTokenConfig;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findBySelectorAndRevokedFalse(String selector);
    void deleteByUser_Id(Long userId); // optional helper for logout-all
}
