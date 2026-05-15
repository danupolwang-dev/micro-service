package com.example.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(\"(?i)password\"\\s*:\\s*\")([^\"]+)(\")");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // ดึง Correlation ID ที่ CorrelationIdFilter สร้างไว้ให้แล้ว
        String correlationId = exchange.getRequest().getHeaders().getFirst(CorrelationIdFilter.CORRELATION_ID_HEADER);
        
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        String ip = request.getRemoteAddress() != null ? 
                    request.getRemoteAddress().getAddress().getHostAddress() : "Unknown";
        long startTime = System.currentTimeMillis();

        // 1. กรณี GET: เก็บ Query String
        if (request.getMethod() == HttpMethod.GET) {
            String query = request.getURI().getQuery();
            log.info("[{}] Incoming Request: method={} path={} ip={} query={}", 
                    correlationId, method, path, ip, query != null ? query : "");
            return chain.filter(exchange).then(logResponse(exchange, path, startTime, correlationId));
        }

        // 2. กรณีไม่ใช่ GET: เก็บ Body
        MediaType contentType = request.getHeaders().getContentType();
        boolean isLoggable = contentType != null && 
                (contentType.includes(MediaType.APPLICATION_JSON) || 
                 contentType.includes(MediaType.TEXT_PLAIN));

        if (isLoggable) {
            return ServerWebExchangeUtils.cacheRequestBody(exchange, (serverHttpRequest) -> {
                return serverHttpRequest.getBody()
                        .map(buffer -> {
                            byte[] bytes = new byte[buffer.readableByteCount()];
                            buffer.read(bytes);
                            return new String(bytes, StandardCharsets.UTF_8);
                        })
                        .reduce(new StringBuilder(), StringBuilder::append)
                        .map(StringBuilder::toString)
                        .defaultIfEmpty("")
                        .flatMap(body -> {
                            String maskedBody = maskSensitiveData(body);
                            log.info("[{}] Incoming Request: method={} path={} ip={} body={}", 
                                    correlationId, method, path, ip, maskedBody);
                            
                            return chain.filter(exchange.mutate().request(serverHttpRequest).build());
                        });
            }).then(logResponse(exchange, path, startTime, correlationId));
        }

        // 3. กรณีอื่นๆ
        log.info("[{}] Incoming Request: method={} path={} ip={} (Body not logged)", correlationId, method, path, ip);
        return chain.filter(exchange).then(logResponse(exchange, path, startTime, correlationId));
    }

    private Mono<Void> logResponse(ServerWebExchange exchange, String path, long startTime, String correlationId) {
        return Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            int statusCode = exchange.getResponse().getStatusCode() != null ? 
                             exchange.getResponse().getStatusCode().value() : 0;
            log.info("[{}] Response: path={} status={} duration={}ms", correlationId, path, statusCode, duration);
        });
    }

    private String maskSensitiveData(String body) {
        if (body == null || body.isEmpty()) {
            return body;
        }
        Matcher matcher = PASSWORD_PATTERN.matcher(body);
        if (matcher.find()) {
            return matcher.replaceAll("$1******$3");
        }
        return body;
    }

    @Override
    public int getOrder() {
        // ทำงานต่อจาก CorrelationIdFilter (ซึ่งเป็น HIGHEST_PRECEDENCE)
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
