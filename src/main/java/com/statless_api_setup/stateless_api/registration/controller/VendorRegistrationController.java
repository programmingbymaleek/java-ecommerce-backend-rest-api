package com.statless_api_setup.stateless_api.registration.controller;

import com.statless_api_setup.stateless_api.registration.dto.VendorRegistrationRequest;
import com.statless_api_setup.stateless_api.registration.service.RegistrationService;
import com.statless_api_setup.stateless_api.user.UserEntity;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/vendor")
public class VendorRegistrationController {
    private final RegistrationService registrationService;

    public VendorRegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }
    @PostMapping(name = "registration")
    public ResponseEntity<?> registerVendors(@Valid @RequestBody VendorRegistrationRequest request){
        registrationService.registerVendor(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
