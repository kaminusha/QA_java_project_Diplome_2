package ru.yandex.praktikum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.models.UserPojo;
import ru.yandex.praktikum.steps.UserSteps;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

// Класс тестирования сценариев авторизации пользователя (позитивные и негативные проверки)
public class UserAuthorizationScenarioTests extends BaseTest {
    private final UserSteps userSteps = new UserSteps();
    private UserPojo user;
    private String createdAccessToken;

    // Подготовка перед каждым тестовым запуском
    @Before
    public void setUp() {
        // Создаем пользователя с рандомными данными, регистрируем его и сохраняем токен
        user = new UserPojo();
        String randomLocalPart = RandomStringUtils.randomAlphanumeric(5);
        String domain = "example.com";
        String email = randomLocalPart + "+" + System.currentTimeMillis() + "@" + domain;
        user
                .setEmail(email)
                .setPassword(RandomStringUtils.randomAlphanumeric(11))
                .setName(RandomStringUtils.randomAlphanumeric(10));
        ValidatableResponse createResponse = userSteps.createUser(user).statusCode(SC_OK);
        createdAccessToken = userSteps.extractAccessToken(createResponse); // <-- ТОКЕН ПОЛУЧАЕМ ЗДЕСЬ!

        // Сохраняем токен в объект пользователя для последующих запросов
        user.setAccessToken(createdAccessToken);
    }

    // Тест - проверяем успешную авторизацию пользователя
    @Test
    @DisplayName("Позитивный тест на успешную авторизацию пользователя")
    @Description("Пользователь успешно авторизуется")
    public void testSuccessfulUserLogin() {
        ValidatableResponse response = userSteps.loginUser(user).statusCode(SC_OK);
        response.body("accessToken", notNullValue()); // Проверяем, что токен выдан
    }

    // Тест - проверяем попытку авторизации с некорректным email
    @Test
    @DisplayName("Негативный тест на невозможность авторизации  пользователя с неверным email")
    @Description("Пользователь авторизуется без email")
    public void testLoginWithWrongEmail() {
        user.setEmail("wrong_email@example.com"); // Подставляем некорректный email
        userSteps
                .loginUser(user)
                .statusCode(SC_UNAUTHORIZED) // Ожидаем код ошибки 401
                .body("message", equalTo("email or password are incorrect")); // Проверяем текст ошибки
    }

    // Тест - проверяем поппытку авторизации с некорректным паролем
    @Test
    @DisplayName("Негативный тест на невозможность авторизации  пользователя без пароля")
    @Description("Пользователь авторизуется без пароля")
    public void testLoginWithWrongPassword() {
        user.setPassword("wrong_password"); // Меняем пароль на неверный
        userSteps
                .loginUser(user)
                .statusCode(SC_UNAUTHORIZED) // Ожидаем код ошибки 401
                .body("message", equalTo("email or password are incorrect")); // Проверяем текст ошибки
    }


    // После каждого теста — очищает тестовые данные
    @After
    public void tearDown() {
        if (createdAccessToken != null && !createdAccessToken.isEmpty()) {
            userSteps.deleteUser(user).statusCode(SC_ACCEPTED); // // Удаляем пользователя через API
            System.out.println("Пользователь успешно удалён.");
        } else {
            System.out.println("Токен не был получен. Удаление пользователя пропущено.");
        }
    }

}
