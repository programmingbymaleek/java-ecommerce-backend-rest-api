package com.statless_api_setup.stateless_api.user;

public enum Role {
    USER,
    ADMIN;
    public String getAuthority(){
        return "ROLE_"+this.name();
    }
}
