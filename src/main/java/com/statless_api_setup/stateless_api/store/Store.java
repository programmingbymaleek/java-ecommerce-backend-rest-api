package com.statless_api_setup.stateless_api.store;

import com.statless_api_setup.stateless_api.vendor.Vendor;
import jakarta.persistence.*;

@Entity
@Table(name = "stores", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"slug"}),
        @UniqueConstraint(columnNames = {"storeName"})
})
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "vendor_id",nullable = false,unique = true)
    private Vendor vendor;
    @Column(nullable = false,unique = true)
    private String storeName;
    @Column(nullable = true, unique = true)
    private String slug;
    private String logoUrl;
    private String description;
    private boolean active = true;

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
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
}
