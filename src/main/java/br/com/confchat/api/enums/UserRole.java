package br.com.confchat.api.enums;

public enum UserRole {
    ADMIN("admin"),
    USER("user"),
    MERCHANT("merchant");
    private String role;
    UserRole(String role) {
        this.role = role;
    }
    public String getRole(){
        return role;
    }
}
