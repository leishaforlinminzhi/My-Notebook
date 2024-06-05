package com.example.mynotebook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mynotebook.tools.PhotoAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class DetailActivity extends AppCompatActivity {

    private PhotoAdapter photoAdapter;
    private List<Bitmap> selectedPhotos;
    private GridView gridViewPhotos;
    private MediaPlayer mediaPlayer;
    private String voiceFilePath;
    private ChipGroup chipGroupTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        selectedPhotos = new ArrayList<>();
        gridViewPhotos = findViewById(R.id.gridViewPhotos);
        photoAdapter = new PhotoAdapter(this, selectedPhotos);
        gridViewPhotos.setAdapter(photoAdapter);

        // 获取从上一个 Activity 传递过来的笔记信息
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString("title");
            String text = extras.getString("text");
            String tags = extras.getString("tags");

            int id = extras.getInt("id");
            int noteID = extras.getInt("noteID");
            String images = extras.getString("images");
            voiceFilePath = extras.getString("voice");

            // 在 TextView 中显示笔记信息
            TextView textViewTitle = findViewById(R.id.textViewTitle);
            TextView textViewText = findViewById(R.id.textViewText);
            chipGroupTags = findViewById(R.id.chipGroupTags);

            textViewTitle.setText(title);
            textViewText.setText(text);

            // 显示标签
            if (tags != null && !tags.isEmpty()) {
                String[] tagArray = tags.replace("[","").replace("]","").split(",");
                for (String tag : tagArray) {
                    addChipToGroup(tag);
                }
            }

            // 加载图片
            if (images != null && !images.isEmpty()) {
                String[] imagePaths = images.replace("[", "").replace("]", "").split(",");
                for (String path : imagePaths) {
                    File imgFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), path.trim());
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        selectedPhotos.add(bitmap);
                    }
                }
                photoAdapter.notifyDataSetChanged();
            }
        }

        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());

        Button buttonPlayVoice = findViewById(R.id.buttonPlayVoice);
        buttonPlayVoice.setOnClickListener(v -> playVoice());
    }

    private void addChipToGroup(String tag) {
        Chip chip = new Chip(this);
        chip.setText(tag);
        chip.setCheckable(false);  // 设置为不可选择，根据需要可改为true
        chipGroupTags.addView(chip);
    }

    private void playVoice() {
        if (voiceFilePath != null && !voiceFilePath.isEmpty()) {
            File voiceFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), voiceFilePath);
            if (voiceFile.exists()) {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                mediaPlayer = new MediaPlayer();
                try {
                    Toast.makeText(this, "语音播放中", Toast.LENGTH_SHORT).show();
                    mediaPlayer.setDataSource(voiceFile.getAbsolutePath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Voice file not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No voice file provided", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
