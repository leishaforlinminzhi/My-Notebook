package com.example.mynotebook.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mynotebook.GlobalValue;
import com.example.mynotebook.R;

import java.util.ArrayList;
import java.util.List;

public class fragment_add extends Fragment {
    private Integer id = null;
    private static final int REQUEST_CODE_SELECT_PHOTOS = 100;
    private static final int REQUEST_CODE_TAKE_PHOTO = 101;

    private GridView gridViewPhotos;
    private Button btnAddPhotos;
    private List<Uri> selectedPhotos;
    private ArrayAdapter<Uri> photoAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);


        GlobalValue app = (GlobalValue) requireActivity().getApplication();;
        id = app.getId();

        gridViewPhotos = view.findViewById(R.id.gridViewPhotos);
        btnAddPhotos = view.findViewById(R.id.btnAddPhotos);

        selectedPhotos = new ArrayList<>();
        photoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, selectedPhotos);
        gridViewPhotos.setAdapter(photoAdapter);

        gridViewPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 处理照片点击事件，例如查看大图或者删除照片
                Uri photoUri = selectedPhotos.get(position);
                Toast.makeText(requireContext(), "Clicked: " + photoUri.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        btnAddPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动相册选择照片
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTOS);
            }
        });



        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_PHOTOS && resultCode == getActivity().RESULT_OK) {
            // 处理选择的照片
            if (data != null) {
                if (data.getData() != null) {
                    // 单张照片
                    Uri selectedPhoto = data.getData();
                    selectedPhotos.add(selectedPhoto);
                } else if (data.getClipData() != null) {
                    // 多张照片
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri selectedPhoto = data.getClipData().getItemAt(i).getUri();
                        selectedPhotos.add(selectedPhoto);
                    }
                }
                photoAdapter.notifyDataSetChanged();
            }
        }
    }
}
