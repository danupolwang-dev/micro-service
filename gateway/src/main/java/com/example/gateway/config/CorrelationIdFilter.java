package com.example.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    public static final String CORRELATION_ID_HEADER = "x-correlation-id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = getOrGenerateCorrelationId(exchange);

        // ใส่ ID ลงใน Request Header (เพื่อส่งต่อให้ Service ปลายทาง)
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate().header(CORRELATION_ID_HEADER, correlationId).build())
                .build();

        // ใส่ ID ลงใน Response Header (เพื่อส่งกลับให้ Client)
        mutatedExchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, correlationId);

        return chain.filter(mutatedExchange);
    }

    private String getOrGenerateCorrelationId(ServerWebExchange exchange) {
        String id = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (id == null || id.isEmpty()) {
            return UUID.randomUUID().toString();
        }
        return id;
    }

    @Override
    public int getOrder() {
        // ให้ทำงานก่อน LoggingFilter (LoggingFilter ใช้ HIGHEST_PRECEDENCE ซึ่งคือ -2147483648)
        // เราจึงต้องตั้งค่าน้อยกว่านั้น หรือถ้า LoggingFilter เป็น HIGHEST_PRECEDENCE เราก็ตั้งเป็น HIGHEST_PRECEDENCE - 1 ไม่ได้ (เพราะมันต่ำสุดแล้ว)
        // แต่จริงๆ แล้ว Ordered.HIGHEST_PRECEDENCE คือค่าต่ำสุดที่เป็นไปได้
        // ดังนั้นเราจะให้ CorrelationIdFilter เป็น HIGHEST_PRECEDENCE
        // และให้ LoggingFilter เป็น HIGHEST_PRECEDENCE + 1 แทน
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
