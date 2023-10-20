package com.newsline.dao;

import com.newsline.dto.CommentFromServer;
import com.newsline.models.Comment;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentsDAO {
    List<CommentFromServer> findAllByNewsId(UUID newsId);
    Optional<Comment> getCommentById(UUID id);
    UUID getAuthorIdById(UUID id);
    void save(Comment comment);
    void update(Comment comment);
    void delete(UUID id);
}
