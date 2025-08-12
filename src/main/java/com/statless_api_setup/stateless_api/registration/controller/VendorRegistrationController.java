package com.statless_api_setup.stateless_api.registration.controller;
import com.statless_api_setup.stateless_api.registration.dto.VendorRegistrationRequest;
import com.statless_api_setup.stateless_api.registration.service.RegistrationService;
import com.statless_api_setup.stateless_api.user.UserEntity;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.statless_api_setup.stateless_api.auth.AuthController;
import com.statless_api_setup.stateless_api.auth.AuthService;
import com.statless_api_setup.stateless_api.registration.dto.VendorRegistrationRequest;
import com.statless_api_setup.stateless_api.registration.service.RegistrationService;
import com.statless_api_setup.stateless_api.user.UserEntity;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Optional;

@RestController
@RequestMapping("/vendor")
public class VendorRegistrationController {
    private final RegistrationService registrationService;
    private final AuthService authService;




    public VendorRegistrationController(RegistrationService registrationService, AuthService authService ) {
        this.registrationService = registrationService;
        this.authService =authService;
    }
    @PostMapping(name = "registration")
    public ResponseEntity<?> registerVendors(@Valid @RequestBody VendorRegistrationRequest request){
        registrationService.registerVendor(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerVendor(@Valid @RequestBody VendorRegistrationRequest request,
                                            HttpServletResponse response) {

       // Register vendor
        System.out.println("Testing before breaking");
        UserEntity user = registrationService.registerVendor(request);

        // Auto-login with the same credentials just registered
        var tokenResponse = authService.login(
                new AuthService.LoginRequest(request.email(), request.password()),
                response
        );

        // 201 + token body; refresh cookie already set by AuthService
        return ResponseEntity.status(201).body(tokenResponse);
    }


}
