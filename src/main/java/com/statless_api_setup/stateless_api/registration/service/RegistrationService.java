package com.statless_api_setup.stateless_api.registration.service;

import com.statless_api_setup.stateless_api.exceptions.InvalidOperationException;
import com.statless_api_setup.stateless_api.registration.dto.VendorRegistrationRequest;
import com.statless_api_setup.stateless_api.roles.RoleEntities;
import com.statless_api_setup.stateless_api.roles.RoleRepository;
import com.statless_api_setup.stateless_api.store.Store;
import com.statless_api_setup.stateless_api.store.StoreRepository;
import com.statless_api_setup.stateless_api.user.UserEntity;
import com.statless_api_setup.stateless_api.user.UserRepository;
import com.statless_api_setup.stateless_api.vendor.Vendor;
import com.statless_api_setup.stateless_api.vendor.VendorRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class RegistrationService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    StoreRepository storeRepository;
    VendorRepository vendorRepository;

    public RegistrationService(UserRepository userRepository,
                               RoleRepository roleRepository,
                               StoreRepository storeRepository,
                               VendorRepository vendorRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.storeRepository = storeRepository;
        this.vendorRepository = vendorRepository;
    }

    @Transactional
    public void registerVendor(VendorRegistrationRequest vendorRegistrationRequest) {
        Optional<UserEntity> existingUser = userRepository.findByEmail(vendorRegistrationRequest.email());
        Optional<Vendor> existingBusinessName = vendorRepository.findByBusinessName(vendorRegistrationRequest.businessId());
        Optional<Store> existingStore = storeRepository.findByStoreName(vendorRegistrationRequest.storeName());

        if (existingStore.isPresent()) {
            throw new InvalidOperationException("Store Name already taken, Please Try another name");
        }
        if (existingBusinessName.isPresent()) {
            throw new InvalidOperationException("Business Id already Exit");
        }

        RoleEntities vendorRole = roleRepository.findByName("VENDOR")
                .orElseThrow(() -> new InvalidOperationException("VENDOR role not found"));

        UserEntity user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            boolean isAlreadyVendor = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("VENDOR"));
            if (isAlreadyVendor) {
                throw new InvalidOperationException("User is already registered as a vendor");
            }
            user.getRoles().add(vendorRole);
            userRepository.save(user); // update user role as well
        } else {
            //create a new User
            user = new UserEntity();
            user.setEmail(vendorRegistrationRequest.email());
            user.setAddress(vendorRegistrationRequest.address());
            user.setPassword(new BCryptPasswordEncoder().encode(vendorRegistrationRequest.password()));
            user.getRoles().add(vendorRole);
            user.setFirstname(vendorRegistrationRequest.firstname());
            user.setLastname(vendorRegistrationRequest.lastname());
            user = userRepository.save(user);
        }
        //create new Vendor
        Vendor vendor = new Vendor();
        vendor.setUser(user);
        vendor.setBusinessId(vendorRegistrationRequest.businessId());

        Store store = new Store();
        store.setStoreName(vendorRegistrationRequest.storeName());
        store.setDescription(vendorRegistrationRequest.description());
        store.setVendor(vendor);
        store.setSlug(generateUniqueSlug(vendorRegistrationRequest.storeName()));
        store.setLogoUrl("");
        //now remember to set vendor entity with details from store created.
        vendor.setStore(store); // link both ways.
        vendorRepository.save(vendor); // this will cascade store since we set cascade =All
    }

    private String generateUniqueSlug(String baseName) {
        String slug = generateSlug(baseName);
        String baseSlug = slug;
        int counter = 1;
        while (storeRepository.existBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }

    private String generateSlug(String input) {
        return input.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-");
    }


}
