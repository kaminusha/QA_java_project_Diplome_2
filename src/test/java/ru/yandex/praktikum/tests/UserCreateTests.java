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
import static org.hamcrest.CoreMatchers.*;

// Класс тестов для проверки функциональности создания пользователей (позитивные и негативные проверки валидации создания учётных записей)
public class UserCreateTests extends BaseTest {
    private final UserSteps userSteps = new UserSteps();
    private UserPojo user;
    private String createdAccessToken;


    // Подготовка перед каждым тестовым запуском
    @Before
    public void setUp() {
        // Генерирует уникальные данные (email, пароль, имя) для нового пользователя
        user = new UserPojo();
        String randomLocalPart = RandomStringUtils.randomAlphanumeric(5);
        String domain = "example.com";
        String email = randomLocalPart + "+" + System.currentTimeMillis() + "@" + domain;
        user
                .setEmail(email)
                .setPassword(RandomStringUtils.randomAlphanumeric(11))
                .setName(RandomStringUtils.randomAlphanumeric(10));
    }

    // Тест на успешное создание пользователя
    @Test
    @DisplayName("Проверка на создание пользователя")
    @Description("Позитивный тест на создание пользователя с заполненными полями")
    public void testUserCreationSuccess() {
        ValidatableResponse response = userSteps.createUser(user).statusCode(SC_OK);
        createdAccessToken = userSteps.extractAccessToken(response); // Сохраняем полученный токен
        response.body("accessToken", notNullValue()); // Проверяем наличие токена
    }

    // Тест попытки создания дубликата пользователя
    @Test
    @DisplayName("Проверка на невозможность создания одинаковых пользователей")
    @Description("Негативный тест на невозможность создание одинаковых пользователей")
    public void testDuplicateUserCreation() {
        ValidatableResponse response = userSteps.createUser(user);
        createdAccessToken = userSteps.extractAccessToken(response);
        userSteps.createUser(user).statusCode(SC_FORBIDDEN).body("message", equalTo("User already exists"));
    }

    // Тест попытки создания пользователя без email
    @Test
    @DisplayName("Проверка на невозможность создание пользователя без email")
    @Description("Негативный тест на невозможность создания пользователя без обязательного  без  поля  email")
    public void testUserCreationWithoutEmail() {
        user.setEmail("");
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    // Тест попытки создать пользователя без пароля
    @Test
    @DisplayName("Проверка на невозможность создание пользователя без пароля")
    @Description("Негативный тест на невозможность создания пользователя без обязательного поля  пароль")
    public void testUserCreationWithoutPassword() {
        user.setPassword("");
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    // Тест попытки создать пользователя без имени
    @Test
    @DisplayName("Проверка на невозможность создание пользователя без имени")
    @Description("Негативный тест на невозможность создания пользователя без обязательного поля  имя")
    public void testUserCreationWithoutName() {
        user.setName("");
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    // Тест попытки создать пользователя без всех обязательных полей
    @Test
    @DisplayName("Проверка на невозможность создание пользователя без емэйла пароля и имени")
    @Description("Негативный тест на невозможность создания пользователя без обязательных  полей  емэйла пароля и имени")
    public void testUserCreationWithEmptyFields() {
        user.setEmail("");
        user.setPassword("");
        user.setName("");
        userSteps
                .createUser(user)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    // Метод очистки после каждого теста
    @After
    public void tearDown() {
        if (createdAccessToken != null && !createdAccessToken.isEmpty()) { // Проверяем наличие валидного токена
            user.setAccessToken(createdAccessToken);
            userSteps.deleteUser(user).statusCode(SC_ACCEPTED);
            System.out.println("Пользователь успешно удалён.");
        } else {
            System.out.println("Пользователь не был создан или токен отсутствует. Удаление не требуется.");
        }
    }
}