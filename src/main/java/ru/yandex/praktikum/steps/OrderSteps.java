package ru.yandex.praktikum.steps;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.config.RestConfig;
import ru.yandex.praktikum.models.OrderPojo;

import java.util.List;

import static io.restassured.RestAssured.given;
import static ru.yandex.praktikum.config.RestConfig.POSTORDERS;

// Работа с API заказов (создание заказа и получение списка ингредиентов)
public class OrderSteps {

    @Step("Создание заказа")
    public ValidatableResponse createOrder(OrderPojo order, String bearerAndToken) {
        return given()
                .spec(UserSteps.getSpec(bearerAndToken))
                .body(order)
                .when()
                .post(POSTORDERS)
                .then();
    }

    @Step("Получение списка ингредиентов")
    public static ValidatableResponse getIngredients() {
        return given()
                .spec(UserSteps.getSpec())
                .when()
                .get(RestConfig.GETINGREDIENTS)
                .then();
    }

    @Step("Получение списка валидных ингредиентов")
    public static List<String> getValidIngredients() {
        return getIngredients().extract().path("data._id");
    }
}

