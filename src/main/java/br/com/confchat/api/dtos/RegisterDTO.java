package br.com.confchat.api.dtos;

import java.time.LocalDate;

public record RegisterDTO(
        String login,
        String name,
        String email,
        String password,
        LocalDate birthDay
) {}
