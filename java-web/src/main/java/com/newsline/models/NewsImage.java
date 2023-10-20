package com.newsline.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "images")
@Setter
@Getter
public class NewsImage {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String name;
    private byte[] content;
    private String type;
    private long size;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "news_id", referencedColumnName = "id")
    private News news;

    public NewsImage(String name, byte[] content, String type, long size) {
        this.name = name;
        this.content = content;
        this.type = type;
        this.size = size;
    }
    public NewsImage(){

    }
}
