package com.newsline.service;

import com.newsline.dao.NewsImageDAO;
import com.newsline.dto.AuthorizedUser;
import com.newsline.dto.NewsDTO;
import com.newsline.dao.NewsDAO;

import com.newsline.exceptions.NewsImagesExtraSizeException;
import com.newsline.models.News;
import com.newsline.models.NewsImage;
import com.newsline.responses.ResponseJSON;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.newsline.common.Utils.isNullOrBlank;

@Service
public final class NewsServiceImpl implements NewsService {
    private final static long MAX_IMAGES_SIZE_BYTE = 1048576;

    private final NewsDAO newsDAO;

    private final NewsImageDAO newsImageDAO;

    public NewsServiceImpl(NewsDAO newsDAO, NewsImageDAO newsImageDAO) {
        this.newsDAO = newsDAO;
        this.newsImageDAO = newsImageDAO;
    }


    @Override
    public ResponseEntity<Map<String, Object>> getAllNews(Integer currentPage, Integer newsOnPage, AuthorizedUser user) {
        ResponseJSON responseJSON = new ResponseJSON();
        OperationStatus status = checkGetParams(currentPage, newsOnPage);

        if (status.httpStatus == HttpStatus.OK) {
            Page<NewsDTO> resultSet = newsDAO.getActualDTONewsOrderByDateAddModifiedMark(user.getId(), user.isSuperUser(),
                    PageRequest.of(currentPage, newsOnPage));

            responseJSON.putToMessageBody("data", resultSet.getContent());
            responseJSON.putToMessageBody("pages", resultSet.getTotalPages());
        }

        responseJSON.putToMainBody("status", status.status);
        responseJSON.putToMainBody("httpStatus", status.httpStatus.value());

        return new ResponseEntity<>(responseJSON.getResponse(), status.httpStatus);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getOneNews(UUID newsId, AuthorizedUser user) {
        ResponseJSON responseJSON = new ResponseJSON();
        String status = "OK";
        HttpStatus httpStatus = HttpStatus.OK;

        if(newsId == null){
            status = "Give me news id path variable";
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        if(httpStatus == HttpStatus.OK){
            NewsDTO resultNews = newsDAO.getNewsByIdAndDeletedFalseAddModifiedMark(newsId, user.getId(), user.isSuperUser());
            if(resultNews == null){
                status = "Wrong news ID";
                httpStatus = HttpStatus.BAD_REQUEST;
            }
            responseJSON.putToMessageBody("data", resultNews);
        }

        responseJSON.putToMainBody("status", status);
        responseJSON.putToMainBody("httpStatus", httpStatus.value());

        return new ResponseEntity<>(responseJSON.getResponse(), httpStatus);
    }

    @Override
    public News findNewsById(UUID id) {
        return newsDAO.findByIdAndDeletedFalse(id);
    }

    @Override
    public ResponseEntity<Map<String, Object>> save(NewsDTO news, AuthorizedUser user) {
        String status = "OK";
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        News newsToSave = null;

        if (isNewsBad(news)) {
            status = "Bad news";
        } else {
            newsToSave = new News(news, user.getId());
            newsToSave.setCreatedBy(user.getId());

            if (!isNewsExist(newsToSave)) {
                newsToSave = newsDAO.save(newsToSave);
                httpStatus = HttpStatus.OK;
            } else {
                status = "News already exists";
            }
        }

        ResponseJSON responseJSON = new ResponseJSON();
        responseJSON.putToMainBody("status", status);
        responseJSON.putToMessageBody("id", (newsToSave == null ? "" : newsToSave.getId()));
        responseJSON.putToMainBody("httpStatus", httpStatus.value());

        return new ResponseEntity<>(responseJSON.getResponse(), httpStatus);
    }

    public ResponseEntity<Map<String, Object>> update(UUID newsId, NewsDTO news, AuthorizedUser user) {
        String status = "News was successfully updated";
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        News newsFromDb = findNewsById(newsId);
        if (newsFromDb == null) {
            status = "Wrong news ID";
        } else if (newsFromDb.getCreatedBy().equals(user.getId()) || user.isSuperUser()) {
            if (isNewsBad(news)) {
                status = "Bad news";
            }

            newsFromDb.setTitle(news.getTitle());
            newsFromDb.setDate(news.getDate());
            newsFromDb.setText(news.getText());
            newsFromDb.setUpdatedBy(user.getId());

            newsDAO.save(newsFromDb);
            httpStatus = HttpStatus.OK;
        } else {
            status = "User can not update this news";
        }

        ResponseJSON responseJSON = new ResponseJSON();
        responseJSON.putToMessageBody("id", newsId);
        responseJSON.putToMainBody("status", status);
        responseJSON.putToMainBody("httpStatus", httpStatus.value());

        return new ResponseEntity<>(responseJSON.getResponse(), httpStatus);
    }

    @Override
    public ResponseEntity<Map<String, Object>> delete(UUID newsId, AuthorizedUser user) {
        String status = "News was successfully deleted";
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        News newsFromDb = findNewsById(newsId);
        if (newsFromDb == null) {
            status = "Wrong news ID";
        } else if (newsFromDb.getCreatedBy().equals(user.getId()) || user.isSuperUser()) {

            newsFromDb.setDeleted(true);
            newsFromDb.setUpdatedBy(user.getId());
            newsDAO.save(newsFromDb);

        } else {
            status = "User can not delete this news";
        }

        ResponseJSON responseJSON = new ResponseJSON();
        responseJSON.putToMainBody("status", status);
        responseJSON.putToMainBody("httpStatus", httpStatus.value());
        return new ResponseEntity<>(responseJSON.getResponse(), httpStatus);
    }

    @Override
    public ResponseEntity<Map<String, Object>> findNewsByQuery(String query, AuthorizedUser user) {
        ResponseJSON responseJSON = new ResponseJSON();
        System.out.println(query);
        String status = "OK";
        HttpStatus httpStatus = HttpStatus.OK;

        if(isNullOrBlank(query)){
            status = "Give me query in body.";
            httpStatus = HttpStatus.BAD_REQUEST;
        }else{
//            var news = newsDAO.findNewsWithFullTextSearch(user.getId(), user.isSuperUser(), query.replace(" ", "&"));
            var news = newsDAO.findNewsWithFullTextSearch(query.replace(" ", "&"));
            responseJSON.putToMessageBody("data", news);
        }


        responseJSON.putToMainBody("status", status);
        responseJSON.putToMainBody("httpStatus", httpStatus.value());
        return new ResponseEntity<>(responseJSON.getResponse(), httpStatus);
    }

    @Override
    public ResponseEntity<Map<String, Object>> saveImage(UUID newsId, MultipartFile file, AuthorizedUser user) {
        String status = "Image was successfully saved";
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        News newsFromDb = findNewsById(newsId);
        if (newsFromDb == null) {
            status = "Wrong news ID";
        } else if (newsFromDb.getCreatedBy().equals(user.getId()) || user.isSuperUser()) {
            try {
                if (file != null && file.getBytes().length != 0) {
                    NewsImage newsImage = new NewsImage(file.getOriginalFilename(), file.getBytes(), file.getContentType(), file.getSize());
                    saveImageToNewsById(newsId, newsImage);
                    httpStatus = HttpStatus.OK;
                }
            } catch (NewsImagesExtraSizeException e) {
                status = "Image has a big size";
            } catch (Exception e) {
                status = "Internal problem with saving image";
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                e.printStackTrace();
            }
        } else {
            status = "User can not add image to this news";
        }

        ResponseJSON responseJSON = new ResponseJSON();
        responseJSON.putToMainBody("status", status);
        responseJSON.putToMainBody("httpStatus", httpStatus.value());

        return new ResponseEntity<>(responseJSON.getResponse(), httpStatus);
    }

    private void saveImageToNewsById(UUID id, NewsImage newsImage) throws NewsImagesExtraSizeException {
        News news = findNewsById(id);
        if (news != null) {
            if (newsImage.getSize() > MAX_IMAGES_SIZE_BYTE) {
                throw new NewsImagesExtraSizeException();
            }
            if (news.getNewsImage() != null) {
                newsImage.setId(news.getNewsImage().getId());
            }
            newsImage.setNews(news);
            newsImageDAO.save(newsImage);
        }
    }

    @Override
    public ResponseEntity<byte[]> getImageById(UUID newsId, UUID imageId) {
        HttpStatus httpStatus = HttpStatus.OK;
        NewsImage image = null;
        News newsFromDb = findNewsById(newsId);

        if (newsFromDb == null) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            image = newsImageDAO.findById(imageId);
            if (image == null) {
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        }

        return new ResponseEntity<>((image == null ? null : image.getContent()), httpStatus);
    }

    private boolean isNewsBad(NewsDTO news) {
        return isNullOrBlank(news.getTitle()) || isNullOrBlank(news.getText()) || news.getDate() == null;
    }

    private boolean isNewsExist(News news) {
        List<News> existedNews = newsDAO.findByTitleAndDateAndDeletedFalse(news.getTitle(), news.getDate());
        return existedNews.size() != 0;
    }

    private OperationStatus checkGetParams(Integer currentPage, Integer newsOnPage) {
        OperationStatus status = new OperationStatus();

        if (currentPage == null || newsOnPage == null) {
            if (currentPage == null) {
                status.setStatus("Give me \"current-page\" param");
            } else {
                status.setStatus("Give me \"news-on-page\" param");
            }
            status.setHttpStatus(HttpStatus.BAD_REQUEST);
        } else {
            if (currentPage < 0) {
                status.setStatus("Give me \"current-page\" param >= 0");
                status.setHttpStatus(HttpStatus.BAD_REQUEST);
            } else if (newsOnPage < 1) {
                status.setStatus("Give me \"news-on-page\" param >= 1");
                status.setHttpStatus(HttpStatus.BAD_REQUEST);
            }
        }
        return status;
    }

    @Getter
    @Setter
    private static class OperationStatus {
        private String status = "OK";
        private HttpStatus httpStatus = HttpStatus.OK;
    }

}
