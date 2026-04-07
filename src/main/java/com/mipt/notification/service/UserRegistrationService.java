package com.mipt.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class UserRegistrationService {

    // chatId -> сессия пользователя
    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();

    // systemUserId -> chatId (для быстрого поиска)
    private final Map<String, Long> userToChatMap = new ConcurrentHashMap<>();

    public static class UserSession {
        private final String systemUserId;
        private final String login;
        private final Long chatId;
        private final long createdAt;

        public UserSession(String systemUserId, String login, Long chatId) {
            this.systemUserId = systemUserId;
            this.login = login;
            this.chatId = chatId;
            this.createdAt = System.currentTimeMillis();
        }

        public String getSystemUserId() { return systemUserId; }
        public String getLogin() { return login; }
        public Long getChatId() { return chatId; }
        public long getCreatedAt() { return createdAt; }
    }

    public void registerUser(String systemUserId, Long chatId, String login) {
        UserSession session = new UserSession(systemUserId, login, chatId);
        sessions.put(chatId, session);
        userToChatMap.put(systemUserId, chatId);
        log.info("✅ User registered: login={}, systemId={}, chatId={}", login, systemUserId, chatId);
    }

    public void unregisterUser(String login) {
        // Находим chatId по логину
        Long chatIdToRemove = null;
        for (UserSession session : sessions.values()) {
            if (session.getLogin().equals(login)) {
                chatIdToRemove = session.getChatId();
                break;
            }
        }

        if (chatIdToRemove != null) {
            UserSession session = sessions.remove(chatIdToRemove);
            if (session != null) {
                userToChatMap.remove(session.getSystemUserId());
                log.info("❌ User unregistered: login={}, chatId={}", login, chatIdToRemove);
            }
        }
    }

    public boolean isAuthenticated(Long chatId) {
        return sessions.containsKey(chatId);
    }

    public Long getChatIdByUserId(String systemUserId) {
        return userToChatMap.get(systemUserId);
    }

    public String getSystemUserIdByChatId(Long chatId) {
        UserSession session = sessions.get(chatId);
        return session != null ? session.getSystemUserId() : null;
    }

    public String getLoginByChatId(Long chatId) {
        UserSession session = sessions.get(chatId);
        return session != null ? session.getLogin() : null;
    }
}