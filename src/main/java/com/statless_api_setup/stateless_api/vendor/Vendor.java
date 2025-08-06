package com.statless_api_setup.stateless_api.vendor;

import com.statless_api_setup.stateless_api.store.Store;
import com.statless_api_setup.stateless_api.user.UserEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "vendors")
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name="user_id",nullable = false,unique = true)
    private UserEntity user;
//    private String address;
    private String businessId; //can also be business registration name // future plan
    // make this a one to one relationship with the store and vendor entity, so that
    //one business name, one store and one vendor.
    @OneToOne(mappedBy = "vendor",cascade = CascadeType.ALL)
    private Store store;
    public UserEntity getUser() {
        return user;
    }
    public void setUser(UserEntity user) {
        this.user = user;
    }
    //can also be business name from CAC
    public String getBusinessId() {
        return businessId;
    }
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
    public Store getStore() {
        return store;
    }
    public void setStore(Store store) {
        this.store = store;
    }


    /**
     * This is designed in such a way that we can achieve the following:
     * <p>
     * From Vendor, you can access their Store. <br>
     * From Store, a Vendor can be accessed. <br>
     * The database enforces uniqueness.
     * </p>
     * <p>
     * - One store per Vendor <br>
     * - One Vendor per User
     * </p>
     * <p>
     * In the future, we will enforce this so that one business ID or CAC
     * is tied to both a Vendor and a Store.
     * </p>
     */










}
