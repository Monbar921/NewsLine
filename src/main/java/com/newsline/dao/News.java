package com.newsline.dao;

import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    private String title;
    private Date date;


    private String text;
    private byte[] image;

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }
    public String getDateAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        return sdf.format(date);
    }

    public String getText() {
        return text;
    }

    public byte[] getImage() {
        return image;
    }

    public String getEncodedImage(){
        return image == null ? null : Base64.getEncoder().encodeToString(image);
    }

}
