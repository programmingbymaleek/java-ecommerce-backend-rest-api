package com.statless_api_setup.stateless_api.store;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store,Long> {
    Optional<Store> findByStoreName(String storeName);
    boolean existBySlug(String Slug);
}
