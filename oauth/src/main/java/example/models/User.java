package example.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;


@Entity
@Table(name = "auth_users")
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@NoArgsConstructor(force = true)
public class User implements UserDetails {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String username;
    private String email;
    private String password;
    private boolean enabled = false;
    private String authorities;
    @OneToOne(mappedBy = "user")
    private Token token;

    public User(String username, String email, String password, List<String> authorities) {
        this.username = username;
        this.email = email;
        this.password = password;
        StringBuilder sb = new StringBuilder();
        authorities.forEach(a->sb.append(a).append(' '));
        this.authorities = sb.toString();
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

//    public User(List<GrantedAuthority> authorities, String username, String password) {
//        this.username = username;
//        this.password = password;
//        this.authorities = new ArrayList<>();
//        authorities.stream().forEach(a -> this.authorities.add(new Authority()));
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String[] listAuthorities = authorities.split(" ");
        Set<GrantedAuthority> list = new HashSet<>();
        Arrays.stream(listAuthorities).forEach(a ->list.add(new SimpleGrantedAuthority(a)));
        list.forEach(System.out::println);
        return list;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", authorities=" + authorities +
                ", token=" + token +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return enabled == user.enabled && Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(authorities, user.authorities) && Objects.equals(token, user.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password, enabled, authorities, token);
    }
}
