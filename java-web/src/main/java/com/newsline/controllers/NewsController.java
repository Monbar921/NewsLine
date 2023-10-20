package com.newsline.controllers;

import com.newsline.dto.*;
import com.newsline.service.CommentsService;
import com.newsline.service.NewsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@Tag(name = "NewsLine controller", description = "The main controller of API")
@Slf4j
public final class NewsController {

    private final NewsService newsService;
    private final CommentsService commentsService;

    public NewsController(NewsService newsService, CommentsService commentsService) {
        this.newsService = newsService;
        this.commentsService = commentsService;
    }

    @Operation(
            summary = "Get all news from database",
            description = "Return news to caller app"
    )
    @RequestMapping(value = "/news/get-all-news", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getAllNews(@RequestParam("current-page") Integer currentPage, @RequestParam("news-on-page") Integer newsOnPage, final JwtAuthenticationToken auth) {
            AuthorizedUser user = new AuthorizedUser(auth.getName());
//        AuthorizedUser user = new AuthorizedUser("innerId:fe9154f9-1c6e-4435-8de2-5e08e2c69de6 isSuperuser:true");
        return newsService.getAllNews(currentPage, newsOnPage, user);
    }

    @RequestMapping(value = "/news/{news_id}/get-news", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getOneNews(@PathVariable("news_id") UUID newsId, final JwtAuthenticationToken auth) {
                AuthorizedUser user = new AuthorizedUser(auth.getName());
//        AuthorizedUser user = new AuthorizedUser("innerId:fe9154f9-1c6e-4435-8de2-5e08e2c69de6 isSuperuser:true");
        return newsService.getOneNews(newsId, user);
    }

    @Operation(
            summary = "Add news to repository"
    )
    @RequestMapping(value = "/news/save-news", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> saveNews(@RequestBody NewsDTO news, final JwtAuthenticationToken auth) {
                AuthorizedUser user = new AuthorizedUser(auth.getName());
//        AuthorizedUser user = new AuthorizedUser("innerId:fe9154f9-1c6e-4435-8de2-5e08e2c69de6 isSuperuser:true");
        return newsService.save(news, user);
    }

    @RequestMapping(value = "/news/{news_id}/update-news", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateNews(@PathVariable("news_id") UUID newsId, @RequestBody NewsDTO news, final JwtAuthenticationToken auth) {
                AuthorizedUser user = new AuthorizedUser(auth.getName());
//        AuthorizedUser user = new AuthorizedUser("innerId:fe9154f9-1c6e-4435-8de2-5e08e2c69de6 isSuperuser:true");
        return newsService.update(newsId, news, user);
    }

    @RequestMapping(value = "/news/{news_id}/delete-news", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> deleteNews(@PathVariable("news_id") @Parameter(description = "id of deleted news") UUID newsId, final JwtAuthenticationToken auth) {
                AuthorizedUser user = new AuthorizedUser(auth.getName());
//        AuthorizedUser user = new AuthorizedUser("innerId:fe9154f9-1c6e-4435-8de2-5e08e2c69de6 isSuperuser:true");
        return newsService.delete(newsId, user);
    }

    @RequestMapping(value = "/news/find-news", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> findNewsByQuery(@RequestParam String query, final JwtAuthenticationToken auth) {
        AuthorizedUser user = new AuthorizedUser(auth.getName());
//        AuthorizedUser user = new AuthorizedUser("innerId:fe9154f9-1c6e-4435-8de2-5e08e2c69de6 isSuperuser:true");
        return newsService.findNewsByQuery(query, user);
    }

    @RequestMapping(value = "/news/{news_id}/save-image", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> saveImage(@PathVariable("news_id") UUID newsId, @RequestBody MultipartFile file, final JwtAuthenticationToken auth) {
                AuthorizedUser user = new AuthorizedUser(auth.getName());
//        AuthorizedUser user = new AuthorizedUser("innerId:fe9154f9-1c6e-4435-8de2-5e08e2c69de6 isSuperuser:true");
        return newsService.saveImage(newsId, file, user);
    }

    @RequestMapping(value = "/news/{news_id}/get-image/{image_id}", method = RequestMethod.GET, produces = {"multipart/form-data"})
    public ResponseEntity<byte[]> getImageById(@PathVariable("news_id") UUID newsId, @PathVariable("image_id") UUID imageId) {
        System.out.println("get image");
        return newsService.getImageById(newsId, imageId);
    }

    @RequestMapping(value = "/news/{news_id}/comments/get-comments", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getCommentsForNews(@PathVariable("news_id") UUID newsId, final JwtAuthenticationToken auth) {
                AuthorizedUser user = new AuthorizedUser(auth.getName());
//        AuthorizedUser user = new AuthorizedUser("innerId:fe9154f9-1c6e-4435-8de2-5e08e2c69de6 isSuperuser:true");
        return commentsService.getCommentsForNews(newsId, user);
    }

    @RequestMapping(value = "/news/{news_id}/comments/save-comment", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> saveComment(@PathVariable("news_id") UUID newsId, @RequestBody CommentToServer comment, final JwtAuthenticationToken auth) {
        AuthorizedUser user = new AuthorizedUser(auth.getName());
        return commentsService.save(newsId, comment, user);
    }

    @RequestMapping(value = "/news/{news_id}/comments/update-comment/{comment_id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateComment(@PathVariable("news_id") UUID newsId, @PathVariable("comment_id") UUID commentId, @RequestBody CommentForUpdate comment, final JwtAuthenticationToken auth) {
                AuthorizedUser user = new AuthorizedUser(auth.getName());
//        AuthorizedUser user = new AuthorizedUser("innerId:fe9154f9-1c6e-4435-8de2-5e08e2c69de6 isSuperuser:true");
        return commentsService.update(newsId, commentId, comment, user);
    }

    @RequestMapping(value = "/news/{news_id}/comments/delete-comment/{comment_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable("news_id") UUID newsId, @PathVariable("comment_id") UUID commentId, final JwtAuthenticationToken auth) {
                AuthorizedUser user = new AuthorizedUser(auth.getName());
//        AuthorizedUser user = new AuthorizedUser("innerId:fe9154f9-1c6e-4435-8de2-5e08e2c69de6 isSuperuser:true");
        return commentsService.delete(newsId, commentId, user);
    }
}
