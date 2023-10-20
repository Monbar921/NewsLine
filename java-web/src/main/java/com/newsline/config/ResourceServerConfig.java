package com.newsline.config;

import com.newsline.common.UserRoles;
import com.newsline.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.retry.support.RetryTemplate;
import java.util.List;

import static org.springframework.security.authorization.AuthenticatedAuthorizationManager.authenticated;
import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasAnyRole;
import static org.springframework.security.authorization.AuthorizationManagers.allOf;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class ResourceServerConfig {
    private final String ADMIN_ROLE = UserRoles.ADMIN.toString();
    private final String USER_ROLE = UserRoles.USER.toString();
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${spring.security.oauth2.oauthserver.jwt.user-info-uri}")
    private String oauthUserInfoUrl;

    private final UserService userService;
    private final RetryTemplate retryTemplate;

    public ResourceServerConfig(UserService userService, RetryTemplate retryTemplate) {
        this.userService = userService;
        this.retryTemplate = retryTemplate;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(withDefaults()).csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/v3/**", "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.GET).hasAnyAuthority("SCOPE_read", "SCOPE_write")
                        .requestMatchers(HttpMethod.POST).hasAuthority("SCOPE_write")
                        .requestMatchers(HttpMethod.PUT).hasAuthority("SCOPE_write")
                        .requestMatchers(HttpMethod.DELETE).hasAuthority("SCOPE_write")
                        .anyRequest().access(allOf(hasAnyRole(ADMIN_ROLE, USER_ROLE), authenticated()))
                )

//                .authorizeHttpRequests((authorize) -> authorize
//                        .requestMatchers("/v3/**", "/swagger-ui/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/save-comment/").permitAll()
//                        .anyRequest().permitAll()
//                )
//                .csrf().disable();
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt()
                        .jwtAuthenticationConverter(
                            new RolesClaimConverter(
                                    new JwtGrantedAuthoritiesConverter(), userService, oauthUserInfoUrl, retryTemplate
                            )
                        ));
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOriginPattern("*");
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }

}