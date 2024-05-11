package com.example.mynotebook.fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mynotebook.GlobalValue;
import com.example.mynotebook.R;

public class LLMFragment extends AppCompatActivity {

    private Integer id = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_llm);

        GlobalValue app = (GlobalValue) getApplication();
        id = app.getId();
    }
}

