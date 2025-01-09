package com.example.outsourcing.domain.common.notification;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@Slf4j
public class WebSocketService {

    private final ConcurrentHashMap<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(Long userId, WebSocketSession session) {
        sessions.put(userId, session);
    }

    public void removeSession(Long userId) {
        sessions.remove(userId);
    }

    public void sendNotificationToUser(Long userId, String message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                } else {
                    log.warn("유저 {}의 웹소켓 세션을 삭제합니다.", userId);
                    removeSession(userId);
                }
            } catch (IOException e) {
                log.error("유저 {}에게 웹소켓 메세지 전송을 실패하였습니다: {}", userId, e.getMessage());
                removeSession(userId);
            }
        } else {
            log.warn("유저 {} 웹소켓 연결 에러", userId);
        }
    }
}
