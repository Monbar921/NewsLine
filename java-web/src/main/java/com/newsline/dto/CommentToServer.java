package com.newsline.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record CommentToServer(UUID newsId, String email, String text, UUID parentComment, Timestamp createdAt) {

}