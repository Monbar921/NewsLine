package example.controllers;

import example.models.User;
import example.service.CustomUsersDetailsService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
public class AuthServerController {
    private final CustomUsersDetailsService userDetailsService;
    public AuthServerController(UserDetailsService userDetailsService) {
        this.userDetailsService = (CustomUsersDetailsService) userDetailsService;
    }

    @PostMapping(value = "/oauth2/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        return userDetailsService.registerUser(user);
    }

    @GetMapping("/oauth2/activate/{token}")
    public ResponseEntity<Map<String, Object>> activate(@PathVariable("token") @NotNull String token) {
        return userDetailsService.finishUserActivation(token);
    }

    @GetMapping("/oauth2/userinfo")
    public ResponseEntity<Map<String, Object>> userinfo(@RequestHeader("Authorization") @NotNull String authorizationHeader) {
        return userDetailsService.getUserDetails(authorizationHeader);
    }

}
