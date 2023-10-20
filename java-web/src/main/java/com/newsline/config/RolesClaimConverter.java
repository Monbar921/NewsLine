package com.newsline.config;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newsline.exceptions.UserNotFoundException;
import com.newsline.models.User;
import com.newsline.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.newsline.common.Utils.isNullOrBlank;

@Slf4j
public class RolesClaimConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final String oauthUserInfoUrl;
    private final JwtGrantedAuthoritiesConverter wrappedConverter;
    private final UserService userService;
    private final RetryTemplate retryTemplate;

    public RolesClaimConverter(JwtGrantedAuthoritiesConverter conv, UserService userService, String oauthUserInfoUrl, RetryTemplate retryTemplate) {
        this.userService = userService;
        wrappedConverter = conv;
        this.oauthUserInfoUrl = oauthUserInfoUrl;
        this.retryTemplate = retryTemplate;
    }

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        // get authorities from wrapped converter
        var grantedAuthorities = new ArrayList<>(wrappedConverter.convert(jwt));
        // get role authorities

        var jwtClaims = jwt.getClaims();

        var roles = (List<String>) jwtClaims.get("roles");
        var scope = (List<String>) jwtClaims.get("scope");
        var username = (String) jwtClaims.get("username");
        var email = (String) jwtClaims.get("email");
        if (roles != null) {
            for (String role : roles) {
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }

        if (scope != null) {
            for (String sc : scope) {
                grantedAuthorities.add(new SimpleGrantedAuthority("SCOPE_" + sc));
            }
        }
        if (roles == null || roles.size() == 0 || scope == null || scope.size() == 0 || isNullOrBlank(username) || isNullOrBlank(email)) {
            throw new IllegalArgumentException();
        }
        User jwtUser = new User(username, email, roles, scope, "");

        String userDetails = userFlow(jwtUser, jwt.getTokenValue());
        log.info(userDetails);
        return new JwtAuthenticationToken(jwt, grantedAuthorities, userDetails);
    }

    private String userFlow(User userFromJwt, String accessToken) {
        String externalPK = null;
        try {
            externalPK = retryTemplate.execute(arg0 -> getUserPKFromOauthServer(accessToken));
        } catch (IOException e) {
            log.warn("Can not retrieve user from auth server");
        }
        System.out.println("_-------");
        System.out.println(externalPK);

        User userFromDb;
        if (externalPK == null) {
            userFromDb = userService.getUserByEmail(userFromJwt.getEmail());
        } else {
            userFromDb = userService.getUserByExternalId(externalPK);
        }
        if (userFromDb == null && externalPK != null) {
            userFromJwt.setExternalId(externalPK);
            userFromDb = userService.saveUserIfNotExists(userFromJwt);
        } else if (userFromDb != null && externalPK != null) {
            userFromJwt.setExternalId(externalPK);
            userFromDb = userService.updateUserIfNotEquals(userFromDb, userFromJwt);
        } else {
            log.warn("Can not authenticate user");
            throw new UserNotFoundException("Try auth later");
        }
        return "innerId:" + userFromDb.getId() + " isSuperuser:" + userFromDb.isSuperuser();
    }

    private String getUserPKFromOauthServer(String accessToken) throws IOException {
        log.info("try retrieve user");
        String PK = null;
        URL url = new URL(oauthUserInfoUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        con.setRequestProperty("Authorization", "Bearer " + accessToken);

        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        int status = con.getResponseCode();

        if (status == HttpStatus.OK.value()) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            PK = readSubFromResponse(content.toString());
            in.close();
        }
        con.disconnect();
        return PK;
    }

    private String readSubFromResponse(String jsonString) {
        if (!isNullOrBlank(jsonString)) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode actualObj = mapper.readTree(jsonString);
                jsonString = actualObj.get("email").toString();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return jsonString.replaceAll("^\"|\"$", "");
    }
}