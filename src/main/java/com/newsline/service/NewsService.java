package com.newsline.service;

import com.newsline.dao.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface NewsService {
    Page<News> getPaginatedNews(Pageable pageable);
    int getNewsOnPageAmount();
    void setNewsOnPageAmount(int pageAmount);
    void saveNews(News news);
    boolean isNewsExist(News news);

}