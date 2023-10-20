package com.newsline.dao;

import com.newsline.models.NewsImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Repository
@Transactional
public interface NewsImageDAO extends CrudRepository<NewsImage, Integer> {
    NewsImage findById(UUID id);
}


