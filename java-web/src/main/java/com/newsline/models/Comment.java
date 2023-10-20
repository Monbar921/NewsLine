package com.newsline.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.newsline.dto.CommentToServer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "comments")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @NotNull
    private UUID newsId;
    @NotNull
    private UUID userId;
    @NotNull
    private String text;
    private UUID parentCommentId;
    @NotNull
    private Timestamp createdAt;
    @NotNull
    private Timestamp modifiedAt;
    private boolean deleted;

    public Comment(CommentToServer comment, @NonNull UUID userId){
        this.newsId = comment.newsId();
        this.userId = userId;
        this.text = comment.text();
        this.parentCommentId = comment.parentComment();
        this.createdAt = comment.createdAt();
        this.modifiedAt = comment.createdAt();
    }
}
