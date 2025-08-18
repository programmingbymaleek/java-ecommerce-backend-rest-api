package com.statless_api_setup.stateless_api.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL) // omit nulls in responses if you ever echo this back
public class UpdateProductRequest {

    @Size(max = 200, message = "Name too long")
    @Pattern(regexp = ".*\\S.*", message = "Name cannot be blank") // only triggers if provided
    private String name;

    @Size(max = 200, message = "Slug too long")//product slug
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must be lowercase, digits, or hyphens")
    private String slug;

    @Size(max = 64, message = "SKU too long")
    @Pattern(regexp = ".*\\S.*", message = "SKU cannot be blank")
    private String sku;

    @PositiveOrZero(message = "Price must be ≥ 0")
    @Digits(integer = 12, fraction = 2, message = "Price must have max 2 decimals")
    private BigDecimal price;

    @PositiveOrZero(message = "Stock must be ≥ 0")
    private Integer stockQuantity;

    @Size(max = 5000, message = "Description too long")
    @Pattern(regexp = ".*\\S.*", message = "Description cannot be blank")
    private String description;

    @Size(max = 2048, message = "Image URL too long")
    private String imageUrl;

    private Boolean active;

    private Long categoryId; // if present, you’ll load & set Category in the service

    // getters & setters


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
