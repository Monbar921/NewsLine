package com.newsline.service;

import com.newsline.dto.AuthorizedUser;
import com.newsline.dto.CommentForUpdate;
import com.newsline.dto.CommentFromServer;
import com.newsline.dto.CommentToServer;
import com.newsline.models.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface CommentsService {
    ResponseEntity<Map<String, Object>> getCommentsForNews(UUID newsId,  AuthorizedUser user);

    ResponseEntity<Map<String, Object>> save(UUID newsId, CommentToServer comment,  AuthorizedUser user);

    ResponseEntity<Map<String, Object>> update(UUID newsId, UUID commentId, CommentForUpdate comment,  AuthorizedUser user);

    ResponseEntity<Map<String, Object>> delete(UUID newsId, UUID commentId,  AuthorizedUser user);
}
