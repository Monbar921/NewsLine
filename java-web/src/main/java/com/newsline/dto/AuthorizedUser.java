package com.newsline.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AuthorizedUser {
    private final UUID id;
    private final boolean isSuperUser;

    public AuthorizedUser(String jwtAuthenticationTokenName){
        String[] parts = jwtAuthenticationTokenName.split(" ");
        this.id = UUID.fromString(parts[0].substring(parts[0].indexOf(":") + 1));

        String superuserRole = parts[1];
        this.isSuperUser = superuserRole.substring(superuserRole.indexOf(":") + 1).equals("true");
    }
}
