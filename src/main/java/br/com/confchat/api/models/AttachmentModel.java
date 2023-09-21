package br.com.confchat.api.models;

import jakarta.persistence.*;

@Entity
@Table(name = "attachment_tb")
public class AttachmentModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "uri")
    private String uri;
    @Column(name = "type_uri")
    private int type;
}
