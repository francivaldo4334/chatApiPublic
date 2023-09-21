package br.com.confchat.api.dtos;

public record ResetPasswordDto(String email,String newPassword,String code){
}
