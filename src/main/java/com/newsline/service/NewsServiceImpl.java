package com.newsline.service;

import com.newsline.dao.News;
import com.newsline.dao.NewsDAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NewsServiceImpl implements NewsService {
    private int pagesAmount = 20;
    @Autowired
    private NewsDAO newsDAO;

    @Override
    public Page<News> getPaginatedNews(Pageable pageable) {
        return newsDAO.findAll(pageable);
    }

    @Override
    public int getPagesAmount() {
        return pagesAmount;
    }

    @Override
    public void setPagesAmount(int pagesAmount) {
        this.pagesAmount = pagesAmount;
    }
    @Override
    public void saveNews(News news){
        if(news.getImage().length == 0){
            news.setImage(null);
        }

    }
}
