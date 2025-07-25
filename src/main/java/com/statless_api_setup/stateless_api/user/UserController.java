package com.statless_api_setup.stateless_api.user;

import com.statless_api_setup.stateless_api.authenticationAndAuthorization.BasicAuthentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    //for testing purposes!!!
    @GetMapping(path = "/getuserdata")
    public ResponseEntity<?> getCurrentUser(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return  ResponseEntity.ok(userDetails.getUsername());
    }
}
