package ru.yandex.praktikum.tests;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import org.junit.Before;

import static ru.yandex.praktikum.config.RestConfig.HOST;

public class BaseTest {

    @Before
    public void startUp(){

        RestAssured.requestSpecification=new RequestSpecBuilder()
                .setBaseUri(HOST)
                .setContentType(ContentType.JSON)
                .build();

        RestAssured.config=RestAssured
                .config()
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
    }
}