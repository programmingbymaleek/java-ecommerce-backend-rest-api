package com.statless_api_setup.stateless_api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserEntity,Long> {
    //Used by UserDetailsService to Authenticate users
    UserEntity findByUsername(String username);
}
