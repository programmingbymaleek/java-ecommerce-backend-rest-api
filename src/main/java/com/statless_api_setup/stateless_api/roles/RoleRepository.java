package com.statless_api_setup.stateless_api.roles;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntities, Long>{
    Optional<RoleEntities> findByName(String name);
}
