package br.com.confchat.api.models;

import br.com.confchat.api.enums.UserRole;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_tb")
public class UserModel implements Serializable, UserDetails {
    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 30, name = "login", columnDefinition = "varchar(50)")
    private String login;
    @Column(nullable = false, name = "password_hash", columnDefinition = "text")
    private String password;
    @Column(length = 50, name = "user_name", columnDefinition = "varchar(50)")
    private String name;
    @Column(length = 255, name = "email", columnDefinition = "varchar(255)")
    private String email;
    @Column(length = 255, name = "description", columnDefinition = "varchar(255)")
    private String description;
    @Column(nullable = false, name = "birth_day", columnDefinition = "date")
    private LocalDate birthDay;
    @CreationTimestamp
    @Column(nullable = false, name = "create_at", columnDefinition = "timestamp")
    private LocalDateTime crateAt;
    @UpdateTimestamp
    @Column(name = "update_at", columnDefinition = "timestamp")
    private LocalDateTime updateAt;
    @Column(nullable = false, name = "active", columnDefinition = "bool")
    private boolean active;
    @Column(nullable = false, name = "fl_two_factor_auth")
    private boolean flTwoFactorAuth;
    @Column(name = "encript_totp_secret")
    private String encriptTotpSecret;
    @Column(nullable = false, name = "user_role", columnDefinition = "varchar(20)")
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Column(name = "last_access")
    private Timestamp lastAccess;
    @Column(nullable = false,name = "number_access",columnDefinition = "int")
    @ColumnDefault("0")
    private int numberAccess;

    public Timestamp getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Timestamp lastAccess) {
        this.lastAccess = lastAccess;
    }

    public int getNumberAccess() {
        return numberAccess;
    }

    public void setNumberAccess(int numberAccess) {
        this.numberAccess = numberAccess;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public boolean isFlTwoFactorAuth() {
        return flTwoFactorAuth;
    }

    public void setFlTwoFactorAuth(boolean flTwoFactorAuth) {
        this.flTwoFactorAuth = flTwoFactorAuth;
    }

    public UserModel() {

    }

    public UserModel(String login,
                     String name,
                     String email,
                     String password,
                     UserRole role,
                     LocalDate birthDay) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.active = false;
        this.birthDay = birthDay;
        this.email = email;
        this.name = name;
        this.crateAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
        this.numberAccess = 0;

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setUserName(String userName) {
        this.login = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        switch (this.role) {
            default -> {
                return List.of(new SimpleGrantedAuthority("ROLE_USER"));
            }
            case MERCHANT -> {
                return List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_MERCHANT"));
            }
            case ADMIN -> {
                return List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_MERCHANT"), new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
        }
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public LocalDateTime getCrateAt() {
        return crateAt;
    }

    public void setCrateAt(LocalDateTime crateAt) {
        this.crateAt = crateAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public boolean getActive() {
        return active;
    }

    public String getEncriptTotpSecret() {
        return encriptTotpSecret;
    }
    public void setEncriptTotpSecret(String encriptTotpSecret) {
        this.encriptTotpSecret = encriptTotpSecret;
    }
}
