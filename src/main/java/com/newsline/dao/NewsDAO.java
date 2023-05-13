package com.newsline.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/*This is the interface to collect information from database*/
@Repository
@Transactional
public interface NewsDAO extends CrudRepository<News, Integer> {
    Page<News> findAllByOrderById(Pageable pageable);
    List<News> findByTitleAndDate(String title, Date date);
}


