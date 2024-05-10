package com.example.backend.service;

import com.example.backend.dao.UserDao;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImple implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public int insert(User user) {
        return userDao.insert(user);
    }

    @Override
    public int deleteById(Integer id) {
        return userDao.deleteById(id);
    }

    @Override
    public int update(User user) {
        return userDao.update(user);
    }

    @Override
    public User getById(Integer id) {
        return userDao.getById(id);
    }

    @Override
    public User login(String username, String password) {
        return userDao.login(username, password);
    }
}
