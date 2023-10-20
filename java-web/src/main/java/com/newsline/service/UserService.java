package com.newsline.service;

import com.newsline.dto.AuthorizedUser;
import com.newsline.models.User;
import org.springframework.stereotype.Service;

import java.rmi.server.UID;

@Service
public interface UserService {
    User saveUserIfNotExists(User user);
    User getUserByEmail(String email);
    User getUserByExternalId(String externalId);
    User updateUserIfNotEquals(User userFromDb, User userFromJwt);
}
