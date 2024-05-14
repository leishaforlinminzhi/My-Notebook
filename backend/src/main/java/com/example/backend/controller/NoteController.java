package com.example.backend.controller;

import com.example.backend.entity.Note;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;


@RestController
@RequestMapping("/record")
public class NoteController {
    @Autowired
    private UserService userService;

    @RequestMapping("/save")
    @ResponseBody
    public Note save(Integer id) {
        Note note = new Note();
        return note;
    }


}

