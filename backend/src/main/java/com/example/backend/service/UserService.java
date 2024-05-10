package com.example.backend.service;

import com.example.backend.entity.User;

public interface UserService {

    int insert(User user);

    int deleteById(Integer id);

    int update(User user);

    User getById(Integer id);

    User login(String username, String password);
}
