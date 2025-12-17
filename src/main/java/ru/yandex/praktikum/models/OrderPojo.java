package ru.yandex.praktikum.models;

import java.util.List;

// Модель заказа для взаимодействия с API Содержит список идентификаторов ингредиентов.
public class OrderPojo {
    private List<String> ingredients;

    public OrderPojo(List<String> ingredients) { this.ingredients = ingredients; }
    public OrderPojo() {}

    public List<String> getIngredients() { return ingredients; }
    public OrderPojo setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
        return this;
    }
}
