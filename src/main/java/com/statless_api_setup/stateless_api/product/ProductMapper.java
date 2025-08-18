package com.statless_api_setup.stateless_api.product;

import com.statless_api_setup.stateless_api.category.Category;
import com.statless_api_setup.stateless_api.store.Store;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class ProductMapper {
    public ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getSlug(),
                p.getSku(),
                p.getDescription(),
                p.getCategory().getId(),
                p.getCategory().getName(),
                p.getStore().getId(),
                p.getPriceCents(),
                p.getStock(),
                p.isActive(),
                p.isDeleted()
        );
    }

    public Product toEntity(CreateProductRequest req, Store store, Category category) {
        Product product = new Product();
        product.setName(req.getName());
        product.setSlug(req.getSlug());
        product.setSku(req.getSku());
        product.setDescription(req.getDescription());
        product.setPriceCents(BigDecimal.valueOf(req.getPrice().movePointRight(2).longValue()));
        product.setStock(req.getStockQuantity());
        product.setActive(true);
        product.setDeleted(false);
        product.setStore(store);
        product.setCategory(category);
        return product;
    }

    public void applyUpdate(Product p,UpdateProductRequest req){
        if(req.getName()!=null)     p.setName(req.getName());
        if(req.getActive()!=null)   p.setActive(req.getActive());
        if(req.getCategoryId()!=null)       p.getCategory().setId(req.getCategoryId());
        if(req.getDescription()!=null)      p.setDescription(req.getDescription());
        if(req.getPrice()!=null)            p.setPriceCents(req.getPrice());
        if(req.getSku()!=null)              p.setSku(req.getSku());
        if(req.getStockQuantity()!=null)    p.setStock(req.getStockQuantity());
        if(req.getImageUrl()!=null)         p.setImageUrl(req.getImageUrl());
    }
}

