package ru.netology.cloudstorage.dtos;

import ru.netology.cloudstorage.enums.UserRole;

public record SignUpDto(
        LogInDto logInDto,
        UserRole role) {
}
