package com.statless_api_setup.stateless_api.product;

import com.statless_api_setup.stateless_api.category.Category;
import com.statless_api_setup.stateless_api.category.CategoryRepository;
import com.statless_api_setup.stateless_api.exceptions.InvalidOperationException;
import com.statless_api_setup.stateless_api.pageResult.PageResult;
import com.statless_api_setup.stateless_api.store.Store;
import com.statless_api_setup.stateless_api.store.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


@Service
public class ProductService {
    /**
     * Validates ownership (vendor can only create/view/edit products in their own store).
     * Handles linking the product to the correct store and category.
     * Applies any business rules (e.g., stock cannot be negative).
     * Calls the repository to save or fetch data.
     * Maps the entity to a DTO (ProductResponse) before returning to the controller.
     */

    private final StoreRepository storeRepo;
    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;
    private final ProductMapper productMapper; // helper to map entity -> DTO

    public ProductService(StoreRepository storeRepo, CategoryRepository categoryRepo, ProductRepository productRepo, ProductMapper productMapper) {
        this.storeRepo = storeRepo;
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
        this.productMapper = productMapper;
    }

    // CREATE product
    public ProductResponse createProduct(Long userId, Long storeId, CreateProductRequest req) {
        // Step 1: Verify store belongs to this vendor
        Store store = storeRepo.findByIdAndVendorUserId(storeId, userId)
                .orElseThrow(() -> new AccessDeniedException("You do not own this store"));

        // Step 2: Verify category exists
        Category category = categoryRepo.findById(req.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        // Step 3: Map request to Product entity
        Product product = new Product();
        product.setStore(store);
        product.setCategory(category);
        product.setName(req.getName());
        product.setSlug(req.getSlug());
        product.setSku(req.getSku());
        product.setPriceCents(req.getPrice());
        product.setDescription(req.getDescription());
        product.setActive(true);
        product.setStock(req.getStockQuantity());
        // Step 4: Save and map to DTO
        Product saved = productRepo.save(product);
        return productMapper.toResponse(saved);
    }


    @Transactional
    public PageResult<ProductResponse> myProducts(Long userId, Long storeId, Pageable pageable) {
        // Guard: store must belong to this vendor
        storeRepo.findByIdAndVendorUserId(storeId, userId)
                .orElseThrow(() -> new AccessDeniedException("You do not own this store"));
        Page<ProductResponse> page = productRepo
                .findByStore_IdAndStore_Vendor_User_IdAndDeletedFalse(storeId, userId, pageable)
                .map(productMapper::toResponse);
        return PageResult.from(page, pageable);
    }

    @Transactional
    public void updateProduct(Long userId, Long storeId, Long productId,UpdateProductRequest req){
        //check to make sure store belongs to said user before updating the product.
        storeRepo.findByIdAndVendorUserId(storeId,userId).orElseThrow(()->new AccessDeniedException("You do not own this store"));
        Product p = productRepo.findByIdAndStore_IdAndStore_Vendor_User_Id(productId,storeId,userId).orElseThrow(()->new AccessDeniedException("Product not fount in your store"));

        //normalize inputs (trim, lower slug)
        String newSku = req.getSku() !=null ? req.getSku().trim():null;
        String newSlug = req.getSlug() !=null ? req.getSlug().trim():null;
        // check uniqueness if changing..
        if(newSku !=null && !newSku.equalsIgnoreCase(p.getSku())){
            boolean taken = productRepo.existsByStore_IdAndSkuIgnoreCaseAndIdNot(storeId,newSku,p.getId());
            if(taken){
                throw new InvalidOperationException("SKU already exists in this store");
            }
            p.setSku(newSku);
        }
        if(newSlug !=null && !newSlug.equalsIgnoreCase(p.getSlug())){
            boolean taken = productRepo.existsByStore_IdAndSlugIgnoreCaseAndIdNot(storeId,newSlug,p.getId());
            if(taken){
                throw new InvalidOperationException("Slug already exists in this store");
            }
            p.setSlug(newSlug);
        }



    }

}
