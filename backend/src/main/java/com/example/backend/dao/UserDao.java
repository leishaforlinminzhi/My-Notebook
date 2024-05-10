package com.example.backend.dao;

import com.example.backend.entity.User;

public interface UserDao {
    /**增**/
    int insert(User user);
    /**删**/
    int deleteById(Integer id);
    /**改**/
    int update(User user);
    /**查**/
    User getById(Integer id);
    /**登录**/
    User login(String username, String password);
}
