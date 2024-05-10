package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/save")
    @ResponseBody
    public User save() {
        User user = new User();
        int id = new Random().nextInt(10000);
        user.setId(id);
        user.setUsername("张三" + id);
        user.setPassword("zhangsan" + id);

        int result = this.userService.insert(user);
        System.out.println(result);
        return user;
    }

    @RequestMapping("/deleteById")
    public void deleteById(Integer id) {
        int result = this.userService.deleteById(id);
        System.out.println(result);
    }

    @RequestMapping("/update")
    public void update() {
        User user = new User();
        user.setId(1);
        user.setPassword("test123");
        this.userService.update(user);
    }

    @RequestMapping("/getById")
    @ResponseBody
    public User getById(Integer id) {
        User user = this.userService.getById(id);
        System.out.println(user.getUsername());
        return user;
    }

    @RequestMapping("/login")
    @ResponseBody
    public User login(String username, String password){
        User user = this.userService.login(username, password);
        System.out.println(user.toString());
        return user;
    }
}
