package com.newsline.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NewsDAO extends CrudRepository<News, Integer> {
    List<News> findAllByOrderById();
}


