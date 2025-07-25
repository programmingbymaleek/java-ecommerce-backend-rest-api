package com.statless_api_setup.stateless_api.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDetailsRepository userDetailsRepository;

    public CustomUserDetailsService(UserDetailsRepository userDetailsRepository){
        this.userDetailsRepository =userDetailsRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity user = userDetailsRepository.findByUserName(username);
        if(user ==null) throw new UsernameNotFoundException("User Not Found");
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().getAuthority()))
        );

    }

}
