package com.statless_api_setup.stateless_api.product;

import com.statless_api_setup.stateless_api.JWTSecurityConfiguration.JwtPrincipal;
import com.statless_api_setup.stateless_api.pageResult.PageResult;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendor/stores/{storeId}/products")
@PreAuthorize("hasRole('VENDOR')") // fence: only vendors can hit these endpoints
public class CreateProductController {

    private final ProductService productService;

    public CreateProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add_new_product")
    public ResponseEntity<ProductResponse> create(
            @PathVariable Long storeId,
            @Valid @RequestBody CreateProductRequest body,
            @AuthenticationPrincipal(expression = "id") Long userId // from JwtPrincipal
    ) {
        ProductResponse created = productService.createProduct(userId, storeId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/products")
    public PageResult<ProductResponse> viewProduct(
            @PageableDefault(size = 10) Pageable pageable, @PathVariable Long storeId, @AuthenticationPrincipal JwtPrincipal principal) {
        System.out.println("hit the my product controller");
        Long userId = principal.id(); //from token
        System.out.println("This is the user id::" + userId);
        return productService.myProducts(userId, storeId, pageable);
    }
}
