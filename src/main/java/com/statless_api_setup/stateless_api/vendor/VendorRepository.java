package com.statless_api_setup.stateless_api.vendor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByBusinessId(String businessId);
}
