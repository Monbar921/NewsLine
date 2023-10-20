package com.newsline.common;

public enum UserRoles {
    ADMIN("admin"), USER("user");

    private final String enumName;
    private UserRoles(String enumName) {
        this.enumName = enumName;
    }

    @Override
    public String toString(){
        return enumName;
    }
}
