package com.example.outsourcing.config;

import com.example.outsourcing.domain.common.notification.WebSocketService;
import com.example.outsourcing.domain.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final WebSocketService webSocketService;
    private final JwtUtil jwtUtil;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String token = Objects.requireNonNull(session.getUri()).getQuery().split("=")[1];
        Claims claims = jwtUtil.extractClaims(jwtUtil.substringToken(token));
        Long userId = Long.parseLong(claims.getSubject());

        webSocketService.addSession(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String token = Objects.requireNonNull(session.getUri()).getQuery().split("=")[1];
        Claims claims = jwtUtil.extractClaims(jwtUtil.substringToken(token));
        Long userId = Long.parseLong(claims.getSubject());

        webSocketService.removeSession(userId);
    }

}
