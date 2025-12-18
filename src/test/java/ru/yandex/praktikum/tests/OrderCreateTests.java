package ru.yandex.praktikum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.models.OrderPojo;
import ru.yandex.praktikum.models.UserPojo;
import ru.yandex.praktikum.steps.OrderSteps;
import ru.yandex.praktikum.steps.UserSteps;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;
import static ru.yandex.praktikum.config.RestConfig.HOST;
import static ru.yandex.praktikum.config.RestConfig.POSTORDERS;

// Класс тестов для проверки сценариев создания заказов в системе
public class OrderCreateTests extends BaseTest {

    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();
    private UserPojo user;
    private String createdAccessToken;

    // Подготовка перед каждым тестовым запуском
    @Before
    public void setUp() {
        // Генерируем уникальный email, пароль и имя для тестового аккаунта
        user = new UserPojo();
        String randomLocalPart = RandomStringUtils.randomAlphanumeric(5);
        String domain = "example.com";
        String email = randomLocalPart + "+" + System.currentTimeMillis() + "@" + domain;
        user
                .setEmail(email)
                .setPassword(RandomStringUtils.randomAlphanumeric(11))
                .setName(RandomStringUtils.randomAlphanumeric(10));
        userSteps.createUser(user); // Регистрируем пользователя
    }

    // Тест - успешное создание заказа с авторизацией и корректными ингредиентами
    @Test
    @DisplayName("Создание заказа с авторизацией и правильными ингредиентами")
    @Description("Проверка успешного создания заказа с авторизацией и корректными ингредиентами")
    public void testSuccessfulOrderCreationWithAuth() {
        List<String> validIngredients = OrderSteps.getValidIngredients();
        OrderPojo order = new OrderPojo(validIngredients);

        // Авторизовываемся и получаем токен
        ValidatableResponse loginResponse = userSteps.loginUser(user);
        createdAccessToken = userSteps.extractAccessToken(loginResponse);
        user.setAccessToken(createdAccessToken);

        // Создаём заказ
        ValidatableResponse response = orderSteps.createOrder(order, user.getAccessToken());
        response
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    // Тест попытки создания заказа без авторизации
    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка попытки создать заказ без авторизации")
    public void testAnonymousOrderCreation() {
        List<String> validIngredients = OrderSteps.getValidIngredients();

        // Создаем заказ только с одним правильным ингредиентом
        OrderPojo order = new OrderPojo(List.of(validIngredients.get(0)));

        RequestSpecification requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(HOST)
                .build();

        ValidatableResponse response = given(requestSpec)
                .body(order)
                .when()
                .post(POSTORDERS)
                .then();

        response
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    // Тест попытки создания заказа без ингредиентов
    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка возможности создания заказа без выбора ингредиентов")
    public void testOrderCreationWithoutIngredients() {

        OrderPojo emptyOrder = new OrderPojo(); // Пустой заказ без ингредиентов

        // Авторизовываемся
        ValidatableResponse loginResponse = userSteps.loginUser(user);
        createdAccessToken = userSteps.extractAccessToken(loginResponse);
        user.setAccessToken(createdAccessToken);

        // Отправляем запрос на создание заказа
        ValidatableResponse response = orderSteps.createOrder(emptyOrder, createdAccessToken);
        response
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    // Тест попытки создания заказа с некорректными хешами ингредиентов
    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверка невозможности создания заказа с неверным набором ингредиентов")
    public void testOrderCreationWithInvalidIngredientHashes() {
        List<String> invalidIngredients = List.of("invalid_ingredient_hash");
        OrderPojo fakeOrder = new OrderPojo(invalidIngredients);

        // Авторизовываемся
        ValidatableResponse loginResponse = userSteps.loginUser(user);
        createdAccessToken = userSteps.extractAccessToken(loginResponse);
        user.setAccessToken(createdAccessToken);

        // Отправляем запрос на создание заказа
        ValidatableResponse response = orderSteps.createOrder(fakeOrder, createdAccessToken);
        response
                .statusCode(SC_INTERNAL_SERVER_ERROR)
                .body(containsString("Internal Server Error"));
    }
}