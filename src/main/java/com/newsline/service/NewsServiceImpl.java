package com.newsline.service;

import com.newsline.dao.News;
import com.newsline.dao.NewsDAO;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NewsServiceImpl implements NewsService {
    //This variable stores the value of the displayed news on the page, default value is 10
    private int newsOnPageAmount = 10;
    @Autowired
    private NewsDAO newsDAO;

    /*This method calls method from DAO object, which returns all ordered by id column records from database.
    Return value is list of pages, each page configures (number of news on page is set here) by object of
    Pageable class.*/
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
/*      When it is necessary to save the image from the form is used object of MultipartFile class,
        its method getBytes() returns empty array, which means the image was not transferred. In the database
        the absence of the image corresponds to the null value. This if statement fixed this discrepancy*/
        if (news.getImage().length == 0) {
            news.setImage(null);
        }
        newsDAO.save(news);
    }

    /*  This method calls method from DAO object, which makes select query and finds records from database,
        where titles and dates match the news being added*/
    @Override
    public boolean isNewsExist(News news) {
        return newsDAO.findByTitleAndDate(news.getTitle(), news.getDate()).size() != 0;
    }
}
