package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/save")
    @ResponseBody
    public User save(String username, String password) {
        User user = new User();

        List<String> names = this.userService.getAllUsernames();
        if (names.contains(username)) {
            user.setId(-1);
            user.setUsername(username);
            user.setPassword(password);
            return user;
        }

        int id;
        List<Integer> ids = this.userService.getAllUserIds();
        do {
            id = new Random().nextInt(10000); // 生成随机的 ID
        } while (ids.contains(id));

        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);

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
