package com.statless_api_setup.stateless_api.product;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Public/store listing
    Page<Product> findByStore_IdAndDeletedFalse(Long storeId, Pageable pageable);

    // Filter by exact category inside a store
    Page<Product> findByStore_IdAndCategory_IdAndDeletedFalse(Long storeId, Long categoryId, Pageable pageable);

    // Ownership-safe single fetch (vendor dashboard use)
    Optional<Product> findByIdAndStore_Vendor_User_Id(Long productId, Long userId);

    // Ownership-safe list (vendor dashboard use)
    Page<Product> findByStore_IdAndStore_Vendor_User_IdAndDeletedFalse(Long storeId, Long userId, Pageable pageable);

    Optional<Product> findByIdAndStore_IdAndStore_Vendor_User_Id(Long productId, Long storeId, Long userId);


    // (Optional) search by name/SKU within a store
    // @Query("""
    //   select p from Product p
    //   where p.store.id = :storeId and p.deleted = false
    //     and (lower(p.name) like lower(concat('%', :q, '%'))
    //          or lower(p.sku) like lower(concat('%', :q, '%')))
    // """)
    // Page<Product> searchInStore(Long storeId, String q, Pageable pageable);

    // (Optional) uniqueness checks
    boolean existsByStore_IdAndSku(Long storeId, String sku);
    boolean existsByStore_IdAndSlug(Long storeId, String slug);

    // List all products across the vendor's own store(s) (no storeId needed)
    Page<Product> findByStore_Vendor_User_IdAndDeletedFalse(Long userId, Pageable pageable);

    // Optional: same but constrained to a category (still no storeId)
    Page<Product> findByStore_Vendor_User_IdAndCategory_IdAndDeletedFalse(Long userId, Long categoryId, Pageable pageable);

}
