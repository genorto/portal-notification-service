package com.mipt.notification.client;

import com.mipt.notification.dto.AuthRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestTemplate restTemplate;

    @Value("${user.service.url:http://localhost:8080}")
    private String userServiceUrl;

    /**
     * Проверка учетных данных пользователя
     * @param login логин
     * @param password пароль
     * @return true если учетные данные верны
     */
    public boolean authenticate(String login, String password) {
        try {
            AuthRequest authRequest = new AuthRequest();
            authRequest.setLogin(login);
            authRequest.setPassword(password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<AuthRequest> request = new HttpEntity<>(authRequest, headers);

            // Вызываем эндпоинт аутентификации user-service
            String url = userServiceUrl + "/api/users/authenticate";
            Boolean result = restTemplate.postForObject(url, request, Boolean.class);

            return result != null && result;
        } catch (Exception e) {
            log.error("Ошибка аутентификации пользователя {}: {}", login, e.getMessage());
            return false;
        }
    }

    /**
     * Получение ID пользователя по логину
     */
    public String getUserId(String login) {
        try {
            String encodedLogin = URLEncoder.encode(login, StandardCharsets.UTF_8);
            String url = userServiceUrl + "/api/users/by-login/" + encodedLogin;
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            log.error("Ошибка получения ID пользователя {}: {}", login, e.getMessage());
            return null;
        }
    }

    /**
     * Сохраняет Telegram chatId для пользователя в основном сервисе.
     */
    public void saveTelegramChatId(String userId, Long chatId) {
        try {
            String url = userServiceUrl + "/api/users/" + URLEncoder.encode(userId, StandardCharsets.UTF_8)
                    + "/telegram-chat?chatId=" + chatId;
            restTemplate.postForObject(url, null, Void.class);
            log.info("Saved telegramChatId={} for userId={}", chatId, userId);
        } catch (Exception e) {
            log.error("Failed to save telegramChatId for userId={}: {}", userId, e.getMessage());
        }
    }

    /**
     * Удаляет привязку Telegram chatId при /logout.
     */
    public void removeTelegramChatId(String userId) {
        try {
            String url = userServiceUrl + "/api/users/" + URLEncoder.encode(userId, StandardCharsets.UTF_8)
                    + "/telegram-chat";
            restTemplate.delete(url);
            log.info("Removed telegramChatId for userId={}", userId);
        } catch (Exception e) {
            log.warn("Failed to remove telegramChatId for userId={}: {}", userId, e.getMessage());
        }
    }

    /**
     * Получает Telegram chatId для пользователя из базы данных.
     */
    public Long getTelegramChatId(String userId) {
        if (userId == null || userId.isBlank()) return null;
        try {
            String url = userServiceUrl + "/api/users/" + URLEncoder.encode(userId, StandardCharsets.UTF_8)
                    + "/telegram-chat";
            return restTemplate.getForObject(url, Long.class);
        } catch (Exception e) {
            log.debug("No telegram chatId for userId={}: {}", userId, e.getMessage());
            return null;
        }
    }

    /**
     * Поиск системного userId по Telegram-юзернейму (без @).
     */
    public String getUserIdByTelegramUsername(String telegramUsername) {
        if (telegramUsername == null || telegramUsername.isBlank()) return null;
        String clean = telegramUsername.startsWith("@") ? telegramUsername.substring(1) : telegramUsername;
        try {
            String url = userServiceUrl + "/api/users/by-telegram/" + URLEncoder.encode(clean, StandardCharsets.UTF_8);
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            log.debug("No user found for telegram username @{}: {}", clean, e.getMessage());
            return null;
        }
    }

    /**
     * Получение email пользователя по его системному ID.
     */
    public String getEmailByUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }

        try {
            String encodedUserId = URLEncoder.encode(userId, StandardCharsets.UTF_8);
            String url = userServiceUrl + "/api/users/" + encodedUserId + "/email";
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            log.warn("Не удалось получить email для userId={}: {}", userId, e.getMessage());
            return null;
        }
    }
}