package com.statless_api_setup.stateless_api.product;

public record ProductResponse(
        Long id,
        String name,
        String slug,
        String sku,
        String description,
        Long categoryId,
        String categoryName,
        Long storeId,
        java.math.BigDecimal priceCents,
        Integer stock,
        boolean active,
        boolean deleted
) {}


