package com.statless_api_setup.stateless_api.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store,Long> {
    Optional<Store> findByStoreName(String storeName);
    boolean existBySlug(String slug);
}
