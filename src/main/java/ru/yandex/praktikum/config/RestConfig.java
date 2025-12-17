package ru.yandex.praktikum.config;

// Класс с константами URL-путей API сервиса Stellar Burgers
    public class RestConfig {

    // Базовый URL сервиса
    public static final String HOST = "https://stellarburgers.education-services.ru";

    // Получение списка ингредиентов
    public static final String GETINGREDIENTS = "/api/ingredients";

    // Создание заказа
    public static final String POSTORDERS = "/api/orders";

    // Регистрация пользователя
    public static final String POSTREGISTER = "/api/auth/register";

    // Авторизация пользователя
    public static final String POSTLOGIN = "/api/auth/login";

    // Удаление пользователя
    public static final String DELETUSER = "/api/auth/user";
}
