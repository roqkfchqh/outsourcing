package com.example.outsourcing.config;

import com.example.outsourcing.domain.common.notification.WebSocketService;
import com.example.outsourcing.domain.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketService webSocketService;
    private final JwtUtil jwtUtil;

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new WebSocketHandler(webSocketService, jwtUtil);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/ws")
            .setAllowedOrigins("*");
    }

}
