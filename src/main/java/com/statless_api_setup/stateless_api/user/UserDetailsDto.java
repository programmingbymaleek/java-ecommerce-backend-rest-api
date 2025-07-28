package com.statless_api_setup.stateless_api.user;

public class UserDetailsDto {
    private String userName;
    private String roles;

    public UserDetailsDto(String userName, String roles) {
        this.userName = userName;
        this.roles = roles;
    }

    public String getUserName() {
        return userName;
    }



    public String getRoles() {
        return roles;
    }

}
