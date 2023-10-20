package example.service;

import com.nimbusds.jose.proc.BadJWSException;
import example.dto.TokenDTO;
import example.utils.JwtUtils;
import example.exception.ConfirmaionException;
import example.exception.IncorrectUserException;
import example.exception.UserAlreadyActivatedException;
import example.exception.UserAlreadyExistsException;

import example.models.Token;
import example.models.User;
import example.repository.TokenDAO;
import example.repository.UserDAO;
import example.utils.UserRoles;
import example.utils.UserScopes;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.transaction.Transactional;

import lombok.Setter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.stream.Collectors;

import static example.config.RabbitMQConfig.*;

@Service
public class CustomUsersDetailsService implements UserDetailsService {
    @Value("${users.service.min.password.length}")
    private int MIN_PASSWORD_LENGTH;
    private final UserDAO userRepository;
    private final TokenDAO tokenRepository;
    private final RabbitTemplate rabbitTemplate;
    @Setter
    private JwtDecoder jwtDecoder;

    public CustomUsersDetailsService(UserDAO userRepository, TokenDAO tokenRepository, RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.rabbitTemplate = rabbitTemplate;
        if (userRepository.countAll() == 0) {
            List<String> stringAuthorities = List.of("SCOPE_read", "SCOPE_write",  "ROLE_admin");
            User user = new User("user", "user@mail.ru", "{noop}user", stringAuthorities);
            user.setEnabled(true);
            userRepository.save(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username);
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s does not exists", username));
        }
        return user;
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        String res = "OK";
        HttpStatus status;

        try {
            validateUser(user);
            User userInRepository = userRepository.findUserByEmail(user.getEmail());
            if (userInRepository != null) {
                throw new UserAlreadyExistsException();
            } else {
                giveAuthoritiesToUser(user);
                setNoEncoderForPassword(user);
                userRepository.save(user);
            }

            activateUser(user);
            status = HttpStatus.CREATED;
        } catch (UserAlreadyExistsException e) {
            res = "User already exists";
            status = HttpStatus.BAD_REQUEST;
        } catch (IncorrectUserException e) {
            res = "Incorrect user";
            status = HttpStatus.BAD_REQUEST;
        } catch (DataAccessResourceFailureException e) {
            res = "Try register again";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (UserAlreadyActivatedException e) {
            res = "User already activated";
            status = HttpStatus.BAD_REQUEST;
        }
        Map<String, Object> response = new HashMap<>();
        response.put("message", res);
        return new ResponseEntity<>(response, status);
    }

    public ResponseEntity<Map<String, Object>> finishUserActivation(String emailToken) {
        String message = "Activated";
        HttpStatus status = HttpStatus.OK;
        try {
            Token token = tokenRepository.findTokenByValue(emailToken);

            if (token == null) {
                status = HttpStatus.BAD_REQUEST;
                message = "You gave wrong token";
            } else {
                Claims tokenClaims = JwtUtils.getClaimsFromToken(emailToken);

                long delta = 1000;
                if(Math.abs(tokenClaims.getExpiration().getTime() - token.getExpiresAt().getTime()) > delta ||
                        !tokenClaims.getSubject().equals(token.getUser().getEmail())){
                    throw new ConfirmaionException();
                }

                User user = token.getUser();
                user.setEnabled(true);
                userRepository.save(user);
            }

        } catch (ConfirmaionException e) {
            message = "Your token was expired";
            status = HttpStatus.BAD_REQUEST;
        } catch (Exception e) {
            message = "Internal server problem";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }

    public ResponseEntity<Map<String, Object>> getUserDetails(String authorizationHeader) {
        Map<String, Object> response = new HashMap<>();
        String message = "OK";
        HttpStatus status = HttpStatus.OK;

        if (authorizationHeader == null) {
            message = "Give authorization header";
            status = HttpStatus.BAD_REQUEST;
        }

        if (status == HttpStatus.OK) {
            if (!authorizationHeader.startsWith("Bearer ")) {
                message = "Give bearer header";
                status = HttpStatus.BAD_REQUEST;
            }
        }
        String token = null;
        if (status == HttpStatus.OK) {
            token = authorizationHeader.substring(authorizationHeader.indexOf(' ') + 1);
            if (token.isEmpty()) {
                message = "Give access token";
                status = HttpStatus.BAD_REQUEST;
            }
        }

        if (status == HttpStatus.OK) {
            status = putUserDetailsToResponse(response, token);
        } else {
            response.put("message", message);
        }

        return new ResponseEntity<>(response, status);
    }


    private void activateUser(User user) {
        try {
            Token token = new Token(user);
            token = tokenRepository.save(token);
            rabbitTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, ROUTING_KEY_EMAIL_NOTIFICATION,
                    new TokenDTO(token.getId(), token.getValue(), user.getEmail()));
        } catch (DataAccessResourceFailureException e) {
            rollbackRegistration(user);
            throw e;
        }
    }

    private void giveAuthoritiesToUser(User user) {
        List<String> roles = new ArrayList<>();
        roles.add(UserRoles.user.name());
        roles = roles.stream().map(r -> "ROLE_" + r).collect(Collectors.toList());

        List<String> scopes = new ArrayList<>();
        scopes.add(UserScopes.read.name());
        scopes.add(UserScopes.write.name());
        scopes = scopes.stream().map(s -> "SCOPE_" + s).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();

        roles.forEach(r -> sb.append(r).append(" "));
        scopes.forEach(s -> sb.append(s).append(" "));

        user.setAuthorities(sb.toString());
    }

    private void setNoEncoderForPassword(User user){
        user.setPassword("{noop}" + user.getPassword());
    }

    private void rollbackRegistration(User user) {
        userRepository.delete(user);
    }

    private void validateUser(User user) throws IncorrectUserException{
        if (isNullOrEmpty(user.getUsername()) || isNullOrEmpty(user.getEmail()) || isNullOrEmpty(user.getPassword())) {
            throw new IncorrectUserException();
        }
        if(!user.getEmail().matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")){
            throw new IncorrectUserException();
        }
        if(!isPasswordCorrect(user.getPassword())){
            throw new IncorrectUserException();
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty() || str.isBlank();
    }

    private boolean isPasswordCorrect(String password) {
        return password.replaceAll(" ", "").length() >= MIN_PASSWORD_LENGTH;
    }

    private HttpStatus putUserDetailsToResponse(Map<String, Object> response, String token) {
        HttpStatus status = HttpStatus.OK;
        try {
            Jwt jwt = jwtDecoder.decode(token);
            Map<String, Object> claims = jwt.getClaims();
            User user = findUserByEmail((String) claims.get("email"));
            if(user == null){
                status = HttpStatus.BAD_REQUEST;
                response.put("message", "User not found");
            } else {
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());

                var authorities = user.getAuthorities();

                Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority)
                        .filter(a -> a.startsWith("ROLE_")).map(a -> a.substring(a.indexOf('_') + 1))
                                .collect(Collectors.toSet());
                Set<String> scope = authorities.stream().map(GrantedAuthority::getAuthority)
                        .filter(a -> a.startsWith("SCOPE_")).map(a -> a.substring(a.indexOf('_') + 1))
                        .collect(Collectors.toSet());

                response.put("roles", roles);
                response.put("scope", scope);
            }
        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
            response.put("message", "Some problems with token");
        }
        return status;
    }


    public User findUserByEmail(String email){
        return userRepository.findUserByEmail(email);
    }
}