package com.example.mynotebook;

import android.app.Application;

public class GlobalValue extends Application {
    private Integer id;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }
}
