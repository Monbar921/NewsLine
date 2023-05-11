package com.newsline.service;

import com.newsline.dao.News;
import com.newsline.dao.NewsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NewsServiceImpl implements NewsService {
    private int pageAmount = 1;
    @Autowired
    private NewsDAO newsDAO;

    @Override
    public Page<News> getPaginatedNews(Pageable pageable) {
        return newsDAO.findAll(pageable);
    }

    @Override
    public int getPageAmount() {
        return pageAmount;
    }

    @Override
    public void setPageAmount(int pageAmount) {
        this.pageAmount = pageAmount;
    }
}
