package com.mipt.notification.client;

import com.mipt.notification.dto.AuthRequest;
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
            String url = userServiceUrl + "/api/users/by-login/" + login;
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            log.error("Ошибка получения ID пользователя {}: {}", login, e.getMessage());
            return null;
        }
    }
}