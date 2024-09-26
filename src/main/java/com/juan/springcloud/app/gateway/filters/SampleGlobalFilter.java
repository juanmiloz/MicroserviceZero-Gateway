package com.juan.springcloud.app.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class SampleGlobalFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(SampleGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        logger.info("Executing Global Filter before request PRE");

        exchange.getRequest().mutate().headers( httpHeaders -> {
            httpHeaders.set("token", "ABCDEFG");
        });



        return chain.filter(exchange).then(Mono.fromRunnable( () -> {
            logger.info("Executing Global Filter POST response");
            //FORMA TRADICIONAL
            /*String token = exchange.getRequest().getHeaders().getFirst("token");
            //String token = exchange.getRequest().getHeaders().get("token").get(0);
            if(token != null) {
                logger.info("Token: " + token);
                exchange.getResponse().getHeaders().add("token", value);
            }*/

            //FORMA FUNCIONAL
            Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("token")).ifPresent(value -> {
                logger.info("Token: " + value);
                exchange.getResponse().getHeaders().add("token", value);
            });

            exchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "red").build());
            exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        }));

    }

    @Override
    public int getOrder() {
        return 100;
    }
}
