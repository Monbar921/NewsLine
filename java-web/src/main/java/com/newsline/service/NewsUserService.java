package com.newsline.service;

import com.newsline.dao.UserDAO;
import com.newsline.dto.AuthorizedUser;
import com.newsline.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewsUserService implements UserService{
    @Autowired
    private UserDAO userDAO;

    @Override
    public User saveUserIfNotExists(User user) {
        User userInRepository = getUserByEmail(user.getEmail());
        System.out.println(user);
        if(userInRepository == null){
            userInRepository = userDAO.save(user);
        }
        return userInRepository;
    }

    @Override
    public User getUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    @Override
    public User getUserByExternalId(String externalId) {
        return userDAO.findByExternalId(externalId);
    }

    @Override
    public User updateUserIfNotEquals(User userFromDb, User userFromJwt) {
        if(!userFromDb.equals(userFromJwt)){
            userFromDb.updateFields(userFromJwt);
            userFromDb = userDAO.save(userFromDb);
        }
        return userFromDb;
    }
}
