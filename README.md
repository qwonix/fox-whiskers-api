# Fox Whiskers (Spring REST API)

Серверное приложение, разработанное в дополнение к [дипломной работе](https://github.com/qwonix/fox-whiskers), защищённой на оценку отлично. Приложение позволяет работать 
с меню пиццерии, пунктами выдачи, оформлением заказов и профилем пользователя. Все действия проводятся благодаря HTTP запросам 

## Технологии
* Java 8
* [Spring REST, JPA, Security](https://spring.io/)
* [JSON Web Token](https://jwt.io/)
* [PostgreSQL](https://www.postgresql.org/)

## Загрузка приложения

Последнюю версию приложения можно скачать на [странице релизов](https://github.com/qwonix/fox-whiskers-api/releases).

### Загрузка исходников сервера

```shell
git clone https://github.com/qwonix/fox-whiskers-api.git
cd fox-whiskers-api
```

### Запуск сервера

```shell
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8085
```

## Использование

Для работы приложения необходимо указать данные для подключения к базе данных в `application.properties`.
