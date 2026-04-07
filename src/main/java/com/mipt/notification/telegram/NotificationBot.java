package com.mipt.notification.telegram;

import com.mipt.notification.client.UserServiceClient;
import com.mipt.notification.service.UserRegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class NotificationBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    private final UserRegistrationService registrationService;
    private final UserServiceClient userServiceClient;

    // Состояния диалога для каждого пользователя
    private final Map<Long, AuthState> authStates = new ConcurrentHashMap<>();

    private enum AuthState {
        WAITING_LOGIN,
        WAITING_PASSWORD
    }

    // Временное хранение логина при вводе пароля
    private final Map<Long, String> tempLogin = new ConcurrentHashMap<>();

    public NotificationBot(UserRegistrationService registrationService,
            UserServiceClient userServiceClient) {
        this.registrationService = registrationService;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getUserName();

            log.info("Received from @{}: {}", userName, messageText);

            // Проверяем, находится ли пользователь в процессе аутентификации
            AuthState currentState = authStates.get(chatId);

            if (currentState == AuthState.WAITING_LOGIN) {
                handleLoginInput(chatId, messageText);
            }
            else if (currentState == AuthState.WAITING_PASSWORD) {
                handlePasswordInput(chatId, messageText);
            }
            else {
                handleCommand(chatId, messageText, userName);
            }
        }
    }

    private void handleCommand(Long chatId, String command, String userName) {
        switch (command) {
            case "/start":
                sendMessage(chatId, getWelcomeMessage(userName));
                break;

            case "/login":
                startLogin(chatId);
                break;

            case "/logout":
                logout(chatId);
                break;

            case "/status":
                checkStatus(chatId);
                break;

            default:
                sendMessage(chatId,
                        "❓ Неизвестная команда\n\n" +
                                "Доступные команды:\n" +
                                "/login - войти в систему\n" +
                                "/status - проверить статус\n" +
                                "/logout - выйти из системы");
                break;
        }
    }

    private void startLogin(Long chatId) {
        // Проверяем, не авторизован ли уже пользователь
        if (registrationService.isAuthenticated(chatId)) {
            String userId = registrationService.getSystemUserIdByChatId(chatId);
            sendMessage(chatId, "✅ Вы уже авторизованы как: " + userId);
            return;
        }

        authStates.put(chatId, AuthState.WAITING_LOGIN);
        sendMessage(chatId,
                "🔐 Вход в систему\n\n" +
                        "Введите ваш логин:");
    }

    private void handleLoginInput(Long chatId, String login) {
        tempLogin.put(chatId, login);
        authStates.put(chatId, AuthState.WAITING_PASSWORD);
        sendMessage(chatId, "🔐 Введите пароль:");
    }

    private void handlePasswordInput(Long chatId, String password) {
        String login = tempLogin.get(chatId);

        // Очищаем временные данные
        tempLogin.remove(chatId);
        authStates.remove(chatId);

        // Аутентификация через user-service
        if (userServiceClient.authenticate(login, password)) {
            String userId = userServiceClient.getUserId(login);
            if (userId != null) {
                registrationService.registerUser(userId, chatId, login);
                sendMessage(chatId, String.format(
                        "✅ Вход выполнен успешно!\n\n" +
                                "Добро пожаловать, %s!\n" +
                                "Ваш ID в системе: %s\n\n" +
                                "Теперь вы будете получать уведомления.\n" +
                                "Для выхода используйте /logout",
                        login, userId
                ));
                log.info("User {} successfully logged in with chatId: {}", login, chatId);
            } else {
                sendMessage(chatId,
                        "❌ Ошибка: не удалось получить ID пользователя\n" +
                                "Попробуйте снова: /login");
            }
        } else {
            sendMessage(chatId,
                    "❌ Неверный логин или пароль!\n\n" +
                            "Попробуйте снова: /login");
            log.warn("Failed login attempt for user: {}", login);
        }
    }

    private void logout(Long chatId) {
        if (registrationService.isAuthenticated(chatId)) {
            String login = registrationService.getLoginByChatId(chatId);
            registrationService.unregisterUser(login);
            sendMessage(chatId,
                    "✅ Вы вышли из системы.\n\n" +
                            "Для входа используйте /login");
            log.info("User {} logged out", login);
        } else {
            sendMessage(chatId, "❌ Вы не авторизованы. Используйте /login");
        }
    }

    private void checkStatus(Long chatId) {
        if (registrationService.isAuthenticated(chatId)) {
            String userId = registrationService.getSystemUserIdByChatId(chatId);
            String login = registrationService.getLoginByChatId(chatId);
            sendMessage(chatId, String.format(
                    "✅ Вы авторизованы!\n\n" +
                            "Логин: %s\n" +
                            "ID в системе: %s\n\n" +
                            "Вы будете получать уведомления, связанные с вашим аккаунтом.\n" +
                            "Для выхода используйте /logout",
                    login, userId
            ));
        } else {
            sendMessage(chatId,
                    "❌ Вы не авторизованы.\n\n" +
                            "Для входа используйте /login");
        }
    }

    private String getWelcomeMessage(String userName) {
        return String.format(
                "🤖 Привет, @%s!\n\n" +
                        "Я бот для получения уведомлений от сервиса объявлений.\n\n" +
                        "Для начала работы необходимо авторизоваться:\n" +
                        "/login - вход в систему\n\n" +
                        "Другие команды:\n" +
                        "/status - проверить статус\n" +
                        "/logout - выйти из системы",
                userName != null ? userName : "пользователь"
        );
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
            log.info("Message sent to chatId: {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chatId: {}", chatId, e);
        }
    }
}