package br.com.confchat.api.dtos;

import java.util.UUID;

public record AuthenticationTwoFactorDTO(String loginOrEmail, String password, String totpCode, String deviceName, String deviceId) {
}
