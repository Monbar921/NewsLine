package com.newsline.service;

import com.newsline.dto.AuthorizedUser;
import com.newsline.dto.NewsDTO;

import com.newsline.exceptions.NewsImagesExtraSizeException;
import com.newsline.models.News;
import com.newsline.models.NewsImage;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Service
public interface NewsService {
    ResponseEntity<Map<String, Object>> getAllNews(Integer currentPage, Integer newsOnPage, AuthorizedUser user);
    ResponseEntity<Map<String, Object>> getOneNews(UUID newsId, AuthorizedUser user);
    ResponseEntity<Map<String, Object>> save(NewsDTO news, AuthorizedUser user);
    ResponseEntity<Map<String, Object>> update(UUID newsId, NewsDTO news, AuthorizedUser user);
    ResponseEntity<Map<String, Object>> delete(UUID newsId, AuthorizedUser user) ;
    ResponseEntity<Map<String, Object>> findNewsByQuery(String query, AuthorizedUser user) ;
    ResponseEntity<Map<String, Object>> saveImage(UUID newsId, MultipartFile file, AuthorizedUser user);
    ResponseEntity<byte[]> getImageById(UUID newsId, UUID imageId);
    News findNewsById(UUID newsId);
}