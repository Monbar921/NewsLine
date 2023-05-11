package com.newsline.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;


import org.springframework.data.domain.Pageable;
import java.util.List;

public interface NewsDAO extends CrudRepository<News, Integer> {
    List<News> findAllByOrderById();
    Page<News> findAll(Pageable pageable);
}


