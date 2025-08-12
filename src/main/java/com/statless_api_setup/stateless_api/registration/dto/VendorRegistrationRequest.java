package com.statless_api_setup.stateless_api.registration.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record VendorRegistrationRequest(
        @NotNull(message = "Username is required")
        String username,
        @NotNull
        String firstname,
        @NotNull
        String lastname,
        @NotNull
        @Email(message = "Invalid email format")
        String email,
        @NotNull
        @Size(min = 6, message = "password must be at least 6 characters")
        String password,
        @NotNull(message = "Store name is required")
        String storeName,
        String businessId,
        @NotNull(message = "Enter your address ")
        String address,
        @NotNull(message="Give a solid description of your store")
        String description
) { }
