package br.com.confchat.api.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "contact_tb")
public class ContactModel {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private UserModel user;
    @ManyToOne
    @JoinColumn(name = "user_contact_id",referencedColumnName = "id")
    private UserModel userContact;
    @Column(name = "name")
    private String name;
    @ManyToOne
    @JoinColumn(name = "chat_id",referencedColumnName = "id")
    private ChatModel fkChat;
    @CreationTimestamp
    @Column(name = "create_at")
    private Timestamp createAt;
    @UpdateTimestamp
    @Column(name = "update_at")
    private Timestamp updateAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public UserModel getUserContact() {
        return userContact;
    }

    public void setUserContact(UserModel userContact) {
        this.userContact = userContact;
    }

    public ChatModel getFkChat() {
        return fkChat;
    }

    public void setFkChat(ChatModel fkChat) {
        this.fkChat = fkChat;
    }
}
