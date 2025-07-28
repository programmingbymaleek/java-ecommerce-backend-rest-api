package com.statless_api_setup.stateless_api.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserDetailsController {
    //for testing purposes!!!
    @GetMapping(path = "/getuserdata")
    public ResponseEntity<UserDetailsDto> getCurrentUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserEntity userEntity = userDetails.getUserEntity();
        UserDetailsDto userDetailsDto = new UserDetailsDto(
                userEntity.getUsername(), userEntity.getRole().name()
        );
        return ResponseEntity.ok(userDetailsDto);
    }
}
