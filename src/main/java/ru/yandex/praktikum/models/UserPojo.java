package ru.yandex.praktikum.models;

// Модель пользователя для взаимодействия с API(email, password, name, accessToken)
public class UserPojo {
    private String email;
    private String password;
    private String name;
    private String accessToken;

    public String getEmail() { return email; }
    public UserPojo setEmail(String email) { this.email = email; return this; }

    public String getPassword() { return password; }
    public UserPojo setPassword(String password) { this.password = password; return this; }

    public String getName() { return name; }
    public UserPojo setName(String name) { this.name = name; return this; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}

