package com.example.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    @Value("${jwt.secret}")
    private String secret;

    public AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. Check if Authorization header is missing
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
            }

            // 2. Extract Token
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String token = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else {
                return onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            try {
                // 3. Validate Token & Parse Claims
                Claims claims = Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(token)
                        .getBody();

                // Check Role if configured in application.yml
                if (config.getRole() != null && !config.getRole().isEmpty()) {
                    List<String> roles = claims.get("roles", List.class);
                    boolean hasRole = false;
                    
                    if (roles != null) {
                        if (roles.contains(config.getRole()) || roles.contains("ROLE_" + config.getRole())) {
                            hasRole = true;
                        }
                    }

                    if (!hasRole) {
                         return onError(exchange, "Access Denied: Requires " + config.getRole(), HttpStatus.FORBIDDEN);
                    }
                }
                
                // 4. Extract roles and add to header for downstream services
                List<String> roles = claims.get("roles", List.class);
                String rolesString = "";
                if (roles != null && !roles.isEmpty()) {
                    // Convert List<String> to comma-separated String (e.g., "ADMIN,USER")
                    // Also remove "ROLE_" prefix if present to match database values
                    rolesString = roles.stream()
                            .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                            .collect(Collectors.joining(","));
                }

                // 5. Mutate the request to add the header
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Roles", rolesString)
                        .header("X-User-Id", claims.getSubject()) // Optional: Add User ID too
                        .build();

                // 6. Continue the filter chain with the modified request
                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                // Token invalid, expired, or signature mismatch
                return onError(exchange, "Invalid Token: " + e.getMessage(), HttpStatus.FORBIDDEN);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
        private String role;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
