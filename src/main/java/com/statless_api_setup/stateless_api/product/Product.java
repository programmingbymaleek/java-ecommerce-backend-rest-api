package com.statless_api_setup.stateless_api.product;
import com.statless_api_setup.stateless_api.category.Category;
import com.statless_api_setup.stateless_api.store.Store;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"store_id", "sku"}),
        @UniqueConstraint(columnNames = {"store_id", "slug"})
})

public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)   // <-- DB column on products table
    private Store store;                                // <-- references Store.id

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(nullable=false) private String name;
    @Column(nullable=false) private String slug;
    @Column(nullable=false) private String sku;
    @Column(nullable=false) private BigDecimal priceCents;
    @Column(nullable=false) private Integer stock = 0;
    @Column(columnDefinition = "TEXT") private String description;
    private boolean active = true;
    private boolean deleted = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

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

    public BigDecimal getPriceCents() {
        return priceCents;
    }

    public void setPriceCents(BigDecimal priceCents) {
        this.priceCents = priceCents;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}

