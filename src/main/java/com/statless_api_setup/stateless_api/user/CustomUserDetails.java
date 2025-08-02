package com.statless_api_setup.stateless_api.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails{
    private final UserEntity user;
    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return user.getRoles().stream().map(role->new SimpleGrantedAuthority("ROLE_"+role.getName()))
                .collect(Collectors.toSet());
    }
    @Override
    public String getPassword(){
        return user.getPassword();
    }
    @Override
    public String getUsername(){
        return user.getPassword();
    }
    @Override
    public boolean isEnabled(){
        return user.isEnabled();
    }
    public String getFirstName(){
        return  user.getFirstname();
    }
    public String getLastName(){
        return  user.getLastname();
    }


}