package com.newsline.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    @NotNull
    @Pattern(regexp="^\\S+$", message="Заголовок не должен быть пустым и состоять только из пробелов!")
    private String title;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private Date date;
    @NotNull
    @Pattern(regexp="^\\S+$", message="Содержимое не должно быть пустым и состоять только из пробелов!")
    private String text;

    private byte[] image;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

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
