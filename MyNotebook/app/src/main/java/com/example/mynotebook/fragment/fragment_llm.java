package com.example.mynotebook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mynotebook.GlobalValue;
import com.example.mynotebook.R;

public class fragment_llm extends Fragment {

    private Integer id = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        GlobalValue app = (GlobalValue) requireActivity().getApplication();
        id = app.getId();

        View view = inflater.inflate(R.layout.fragment_llm, container, false);
        // 初始化布局
        return view;
    }
}