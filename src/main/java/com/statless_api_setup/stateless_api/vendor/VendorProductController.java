package com.statless_api_setup.stateless_api.vendor;


import com.statless_api_setup.stateless_api.product.CreateProductRequest;
import com.statless_api_setup.stateless_api.product.ProductResponse;
import com.statless_api_setup.stateless_api.product.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


//@RequestMapping("/vendor/stores/{storeId}/products")
//@PreAuthorize("hasAuthority('SCOPE_ROLE_VENDOR')")
public class VendorProductController {

    private final ProductService productService;
    public VendorProductController(ProductService productService) {
        this.productService = productService;
    }
//    @PostMapping("/add_new_product")
    public ResponseEntity<ProductResponse> createProduct(
            @PathVariable Long storeId,
            @Valid @RequestBody CreateProductRequest body,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(userId, storeId, body));
    }
}

