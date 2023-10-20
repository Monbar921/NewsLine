package example.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import example.utils.JwtUtils;

import example.exception.UserAlreadyActivatedException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "tokens")
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@NoArgsConstructor(force = true)
public class Token {
    private final static long ALIVE_TOKEN_TIME_MILLISECONDS = 3600000;
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String value;
    private Timestamp expiresAt;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Token(User user) throws UserAlreadyActivatedException {
        if(user.isEnabled()){
            throw new UserAlreadyActivatedException();
        }
        this.user = user;
        long expirationMillis = System.currentTimeMillis() + ALIVE_TOKEN_TIME_MILLISECONDS;
        this.expiresAt = new Timestamp(expirationMillis);
        this.value = JwtUtils.generateToken(user, expiresAt);
    }
}
