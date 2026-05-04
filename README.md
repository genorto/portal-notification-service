 # Сервис уведомлений Portal MIPT

Сервис-consumer Kafka для отправки уведомлений пользователям.

## Возможности

- Подписывается на события домена, публикуемые бэкендом.
- Отправляет уведомления по электронной почте и в Telegram.
- Поддерживает вход Telegram-бота и связывание Telegram chat-id с пользователями.
- Обрабатывает события для объявлений, пользователей, чатов, избранного, истории поиска, кошелька, главной страницы и отзывов.

## Каналы доставки

- Email (например, активация аккаунта)
- Telegram-бот (пользовательские уведомления)

## Telegram-бот

Бот поддерживает поток логина и сохраняет `chat-id` пользователей для дальнейших уведомлений.

## Запуск локально

```bash
mvn clean package -DskipTests
mvn spring-boot:run
```

По умолчанию сервис запускается на порту 8082.

## Конфигурация

Важные переменные окружения:

- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `TELEGRAM_BOT_TOKEN`

Списки топиков Kafka и сопоставления типов описаны в `src/main/resources/application.yml`.

## Важные файлы

- `src/main/java/com/mipt/notification/service/NotificationService.java`
- `src/main/java/com/mipt/notification/consumer/`
- `src/main/java/com/mipt/notification/telegram/`
- `src/main/resources/application.yml`
