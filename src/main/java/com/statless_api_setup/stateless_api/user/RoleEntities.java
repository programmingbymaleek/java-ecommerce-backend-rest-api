package com.statless_api_setup.stateless_api.user;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class RoleEntities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}