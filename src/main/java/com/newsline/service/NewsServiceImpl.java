package com.newsline.service;

import com.newsline.dao.News;
import com.newsline.dao.NewsDAO;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NewsServiceImpl implements NewsService {
    //This variable stores the value of the displayed news on the page
    private int newsOnPageAmount = 20;
    @Autowired
    private NewsDAO newsDAO;

    @Override
    public Page<News> getPaginatedNews(Pageable pageable) {
        return newsDAO.findAllByOrderById(pageable);
    }

    @Override
    public int getNewsOnPageAmount() {
        return newsOnPageAmount;
    }

    @Override
    public void setNewsOnPageAmount(int newsOnPageAmount) {
        this.newsOnPageAmount = newsOnPageAmount;
    }

    @Override
    public void saveNews(News news) {
        if (news.getImage().length == 0) {
            news.setImage(null);
        }
        newsDAO.save(news);
    }

    @Override
    public boolean isNewsExist(News news) {
        return newsDAO.findByTitleAndDate(news.getTitle(), news.getDate()).size() != 0;
    }
}
