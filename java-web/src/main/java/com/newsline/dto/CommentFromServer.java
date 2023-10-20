package com.newsline.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CommentFromServer {
    private UUID id;
    private UUID newsId;
    private String username;
    private String text;
    private Timestamp createdAt;
    private int level;
    private boolean deleted;
}