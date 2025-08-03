package com.statless_api_setup.stateless_api.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    private final UserEntity user;

    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Return the user's email or whatever is the identifier
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }


    public String getFirstName() {
        return user.getFirstname();
    }

    public String getLastName() {
        return user.getLastname();
    }
//    eyJraWQiOiI4M2QwMzJlMy00ZWFiLTQ3OTYtOTVhYy05YzlhMTcwODkwN2YiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJXaW1hIEdsb2JhbCBFbnRlcnByaXNlIiwic3ViIjoid2lzZG9tbWFsaWtpQGdtYWlsLmNvbSIsImV4cCI6MTc1NDI0MzEwNiwiaWF0IjoxNzU0MjQyMjA2LCJzY29wZSI6IlJPTEVfVVNFUiBST0xFX0FETUlOIn0.b6Zal7D1y7-2WPJnUAie3twdAdadLhxAXGGlRvkTvFq2Mbbv4nPuR6pQl2xfAVCr-Tjm0KbI0I5bGmzPSAgfKBHDZ3_T-vf_3DaFlKFRVq6VLSgoGwF4X5k8IIQuJSsVSwq9lZG4CZzPYCMWpu0N2LWEOt7G6EOgVBH8ixd1FAZxUKslnQ7kdvw5r0HWM19NhlfdcmUK-REioZCD4Upndo4RpKZeIhxMR6Vd9TIVrObebxc4KoN1y9XWelEn-uvavmHka2K7O4032MU0sOeiRtfVWC86lOQJSN5kXirH-duhb7OJxox5uSKymSsxeFygYzew2oH1gg9MNCwOC422rA

}