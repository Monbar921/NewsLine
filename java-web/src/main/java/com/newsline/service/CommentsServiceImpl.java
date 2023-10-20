package com.newsline.service;


import com.newsline.dao.CommentsDAO;

import com.newsline.dto.AuthorizedUser;
import com.newsline.dto.CommentForUpdate;
import com.newsline.dto.CommentFromServer;
import com.newsline.dto.CommentToServer;
import com.newsline.exceptions.BadCommentException;
import com.newsline.models.Comment;
import com.newsline.models.News;
import com.newsline.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.newsline.responses.ResponseJSON;


import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommentsServiceImpl implements CommentsService{
    private final CommentsDAO commentsDAO;
    private final NewsService newsService;
    private final UserService userService;

    public CommentsServiceImpl(CommentsDAO commentsDAO, NewsService newsService,  UserService userService) {
        this.commentsDAO = commentsDAO;
        this.newsService = newsService;
        this.userService = userService;
    }

    @Override
    public ResponseEntity<Map<String, Object>> getCommentsForNews(UUID newsId,  AuthorizedUser user) {
        HttpStatus httpStatus = HttpStatus.OK;
        String status = "OK";

        var comments = commentsDAO.findAllByNewsId(newsId);
        comments.stream().filter(CommentFromServer::isDeleted).forEach(c -> c.setText("{comment was deleted}"));

        ResponseJSON responseJSON = new ResponseJSON();
        responseJSON.putToMessageBody("data", comments);
        responseJSON.putToMainBody("status", status);
        responseJSON.putToMainBody("httpStatus", httpStatus.value());

        return new ResponseEntity<>(responseJSON.getResponse(), httpStatus);
    }


    private Optional<Comment> getCommentById(UUID id) {
        return commentsDAO.getCommentById(id);
    }

    @Override
    public ResponseEntity<Map<String, Object>> save(UUID newsId, CommentToServer comment, AuthorizedUser user) {
        HttpStatus httpStatus = HttpStatus.OK;
        String status = "OK";

        News news = newsService.findNewsById(newsId);
        if (news == null) {
            status = "News does not exists";
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            Comment commentToSave = new Comment(comment, user.getId());
            commentsDAO.save(commentToSave);
        }

        ResponseJSON responseJSON = new ResponseJSON();
        responseJSON.putToMainBody("status", status);
        responseJSON.putToMainBody("httpStatus", httpStatus.value());

        return new ResponseEntity<>(responseJSON.getResponse(), httpStatus);
    }


    @Override
    public ResponseEntity<Map<String, Object>> update(UUID newsId, UUID commentId, CommentForUpdate comment,  AuthorizedUser user) {
        String status = "Comment was successfully updated";
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        News newsFromDb =  newsService.findNewsById(newsId);

        if(newsFromDb == null){
            status = "Wrong news ID";
        } else {

            Optional<Comment> optionalFromDb = getCommentById(commentId);
            if (optionalFromDb.isEmpty()) {
                status = "Wrong comment ID";
            } else if (optionalFromDb.get().isDeleted()) {
                status = "Cannot edit deleted comment";
            } else {
                Comment commentFromDb = optionalFromDb.get();

                commentFromDb.setText(comment.getText());

                UUID userID = commentFromDb.getUserId();
                if (userID.equals(user.getId()) || user.isSuperUser()) {
                    try {
                        commentsDAO.update(commentFromDb);
                        httpStatus = HttpStatus.OK;
                    } catch (BadCommentException e) {
                        status = "Bad comment";
                    }
                } else {
                    status = "User can not update this comment";
                }
            }

        }

        ResponseJSON responseJSON = new ResponseJSON();
        responseJSON.putToMainBody("status", status);
        responseJSON.putToMainBody("httpStatus", httpStatus.value());

        return new ResponseEntity<>(responseJSON.getResponse(), httpStatus);

    }

    @Override
    public ResponseEntity<Map<String, Object>> delete(UUID newsId, UUID commentId,  AuthorizedUser user) {
        String status = "Comment was successfully deleted";
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        News newsFromDb =  newsService.findNewsById(newsId);

        if(newsFromDb == null){
            status = "Wrong news ID";
        } else {
            Comment comment = commentsDAO.getCommentById(commentId).orElse(null);
            if (comment == null) {
                status = "Wrong comment ID";
            } else if(comment.isDeleted()){
                status = "Comment has already deleted";
            } else if (comment.getUserId().equals(user.getId()) || user.isSuperUser()) {
                commentsDAO.delete(commentId);
                httpStatus = HttpStatus.OK;
            } else {
                status = "User can not delete this comments";
            }
        }

        ResponseJSON responseJSON = new ResponseJSON();
        responseJSON.putToMainBody("status", status);
        responseJSON.putToMainBody("httpStatus", httpStatus.value());
        return new ResponseEntity<>(responseJSON.getResponse(), httpStatus);
    }
}
