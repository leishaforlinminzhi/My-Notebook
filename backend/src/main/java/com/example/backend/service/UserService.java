package com.example.backend.service;

import com.example.backend.entity.User;

import java.util.List;

public interface UserService {

    int insert(User user);

    int deleteById(Integer id);

    int updatePassword(User user);

    int updateSignature(User user);

    int updateName(User user);

    int updateAvatar(User user);

    User getById(Integer id);

    User login(String username, String password);

    List<Integer> getAllUserIds();

    List<String> getAllUsernames();
}
