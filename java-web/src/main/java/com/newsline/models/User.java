package com.newsline.models;

import com.newsline.common.UserRoles;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@With
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private @NonNull String username;
    private @NonNull String email;
    private @NonNull String roles;
    private boolean isSuperuser;
    private @NonNull String scopes;
    private @NonNull String externalId;

    public User(){

    }

    public User(@NonNull String username, @NonNull String email, @NonNull List<String> roles, @NonNull List<String> scopes, @NonNull String externalId) {
        this.username = username;
        this.email = email;
        this.isSuperuser = false;
        int rolesSize = roles.size();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < rolesSize; ++i){
            String elem = roles.get(i);
            if(elem.equals(UserRoles.ADMIN.toString())){
                isSuperuser = true;
            }
            sb.append(elem);
            if (i < rolesSize - 1) {
                sb.append(" ");
            }
        }
        this.roles = sb.toString();

        sb = new StringBuilder();

        for(int i = 0; i < scopes.size(); ++i){
            String elem = scopes.get(i);
            System.out.println(elem);
            sb.append(elem);
            if (i < scopes.size() - 1) {
                sb.append(" ");
            }
        }

        this.scopes = sb.toString();
        this.externalId = externalId;
    }

    public void updateFields(User anotherUser){
        this.username=  anotherUser.getUsername();
        this.email = anotherUser.getEmail();
        this.isSuperuser = anotherUser.isSuperuser();
        this.roles = anotherUser.roles;
        this.scopes = anotherUser.scopes;
        this.externalId = anotherUser.getExternalId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isSuperuser == user.isSuperuser && Objects.equals(username, user.username) &&
                Objects.equals(email, user.email) && Objects.equals(roles, user.roles) &&
                Objects.equals(scopes, user.scopes) && Objects.equals(externalId, user.externalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, roles, isSuperuser, scopes, externalId);
    }
}

