package br.com.confchat.api.models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "device_tb")
public class DeviceModel {
    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private UserModel user;
    @Column(name = "name")
    private String name;
}
