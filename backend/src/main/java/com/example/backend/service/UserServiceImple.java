package com.example.backend.service;

import com.example.backend.dao.UserDao;
import com.example.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


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
    public int updatePassword(User user) {
        return userDao.updatePassword(user);
    }

    @Override
    public int updateSignature(User user) {
        return userDao.updateSignature(user);
    }

    @Override
    public int updateName(User user) {
        return userDao.updateName(user);
    }

    @Override
    public int updateAvatar(User user) {
        return userDao.updateAvatar(user);
    }

    @Override
    public User getById(Integer id) {
        return userDao.getById(id);
    }

    @Override
    public User login(String username, String password) {
        return userDao.login(username, password);
    }

    @Override
    public List<Integer> getAllUserIds() { return userDao.getAllUserIds(); }

    @Override
    public List<String> getAllUsernames(){ return userDao.getAllUsernames(); }
}
