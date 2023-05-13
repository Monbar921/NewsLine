package com.newsline.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;


import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
@Transactional
public interface NewsDAO extends CrudRepository<News, Integer> {
    List<News> findAllByOrderById();
    Page<News> findAll(Pageable pageable);
    Page<News> findAllByOrderById(Pageable pageable);

//    <S extends News> S save(News news);
}


