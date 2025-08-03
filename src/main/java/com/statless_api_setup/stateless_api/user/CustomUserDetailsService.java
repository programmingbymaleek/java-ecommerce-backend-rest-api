package com.statless_api_setup.stateless_api.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Attempting to load user by email: " + email);
        return userRepository.findByEmail(email).map(CustomUserDetails::new)
                .orElseThrow(() -> {
                        System.out.println("User not found: " + email); // Log when user is not found
                       return  new UsernameNotFoundException("User not found: " + email);});
    }
}