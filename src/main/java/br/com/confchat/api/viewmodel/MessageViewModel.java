package br.com.confchat.api.viewmodel;

import br.com.confchat.api.models.UserModel;

import java.sql.Timestamp;
import java.util.UUID;

public class MessageViewModel {
    private String userName;
    private Timestamp createAt;
    private String content;
    private UUID userId;
    private boolean isRead;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
