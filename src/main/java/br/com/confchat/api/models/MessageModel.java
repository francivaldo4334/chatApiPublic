package br.com.confchat.api.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "message_tb")
public class MessageModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "create_at")
    @CreationTimestamp
    private Timestamp createAt;
    @Column(name = "update_at")
    @UpdateTimestamp
    private Timestamp updateAt;
    @Column(name = "content_text")
    private String content;
    @ManyToOne
    @JoinColumn(name = "attachment_id",referencedColumnName = "id")
    private AttachmentModel attachmentModel;
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private UserModel user;
    @ManyToOne
    @JoinColumn(name = "chat_id",referencedColumnName = "id")
    private ChatModel chatModel;
    @Column(name = "is_read")
    private boolean isRead;

    public MessageModel(){
        isRead = false;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public Timestamp getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Timestamp updateAt) {
        this.updateAt = updateAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AttachmentModel getAttachmentModel() {
        return attachmentModel;
    }

    public void setAttachmentModel(AttachmentModel attachmentModel) {
        this.attachmentModel = attachmentModel;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public ChatModel getChatModel() {
        return chatModel;
    }

    public void setChatModel(ChatModel chatModel) {
        this.chatModel = chatModel;
    }
}
