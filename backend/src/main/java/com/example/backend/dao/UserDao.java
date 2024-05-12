package com.example.backend.dao;

import com.example.backend.entity.User;

import java.util.List;

public interface UserDao {
    /**增**/
    int insert(User user);
    /**删**/
    int deleteById(Integer id);
    /**改密码**/
    int updatePassword(User user);
    /**改签名**/
    int updateSignature(User user);
    /**改用户名**/
    int updateName(User user);
    /**改头像**/
    int updateAvatar(User user);
    /**查**/
    User getById(Integer id);
    /**登录**/
    User login(String username, String password);
    /**所有id**/
    List<Integer> getAllUserIds();
    /**所有username**/
    List<String> getAllUsernames();
}
