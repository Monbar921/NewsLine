package com.newsline.dao;

import com.newsline.models.NewsImage;
import com.newsline.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Repository
@Transactional
public interface UserDAO extends CrudRepository<User, UUID> {
    User findByEmail(String email);
    User findByExternalId(String email);
}


