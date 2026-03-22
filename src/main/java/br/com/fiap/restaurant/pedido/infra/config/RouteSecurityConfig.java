package br.com.fiap.restaurant.pedido.infra.config;

import br.com.fiap.restaurant.pedido.infra.controller.response.SimpleErrorResponse;
import br.com.fiap.restaurant.pedido.infra.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true) // Habilita segurança em métodos
public class RouteSecurityConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JwtService jwtService;

    public RouteSecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        final var restaurantUrl = "/restaurants";
        final var menuItemBaseUrl = restaurantUrl + "/{restaurant-menuItemId}/menu";
        final var menuWithIdUrl = restaurantUrl + "/{restaurant-menuItemId}/menu/{menuItemId}";
        final var restaurantWithIdUrl = restaurantUrl + "/{menuItemId}";

        http.httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(req -> req
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/actuator/**"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/auth").permitAll()
                .requestMatchers(HttpMethod.GET, restaurantWithIdUrl, menuItemBaseUrl, menuWithIdUrl).permitAll()
                .anyRequest().authenticated() // Boa prática: fechar com uma regra padrão
            ).addFilterBefore(new TokenAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
            .oauth2ResourceServer(oauth2 ->oauth2.jwt(Customizer.withDefaults()));
        http.exceptionHandling(customizer ->
            customizer.accessDeniedHandler(accessDeniedHandler())
                    .authenticationEntryPoint(authenticationEntryPoint())
        );
        return http.build();
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            var writer = response.getWriter();
            response.setStatus(403);
            response.setContentType("application/json");
            writer.write(objectMapper.writeValueAsString(new SimpleErrorResponse("The current user does not have permission.")));
        };
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            var writer = response.getWriter();
            response.setStatus(401);
            response.setContentType("application/json");
            writer.write(objectMapper.writeValueAsString(new SimpleErrorResponse("User not authenticated. Authentication is required to access this resource.")));
        };
    }
}