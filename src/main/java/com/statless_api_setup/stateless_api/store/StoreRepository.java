package com.statless_api_setup.stateless_api.store;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store,Long> {
    Optional<Store> findByStoreName(String storeName);
    Optional<Store> findBySlug(String slug);
    // Ownership-gated fetch (use this in services before any vendor action)
    Optional<Store> findByIdAndVendorUserId(Long id, Long userId);

    // Sometimes useful if you need "get my store" from a logged-in vendor
    Optional<Store> findByVendorUserId(Long userId);

    boolean existsBySlug(String slug);

}
