package com.example.mynotebook;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mynotebook.tools.PhotoAdapter;
import com.example.mynotebook.utils.HttpPostRequest;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private PhotoAdapter photoAdapter;
    private List<Bitmap> selectedPhotos;
    private GridView gridViewPhotos;
    private MediaPlayer mediaPlayer;
    private String voiceFilePath;
    private ChipGroup chipGroupTags;
    private EditText editTextTitle;
    private EditText editTextText;
    private EditText editTextNewTag;

    private String res_type = "";

    private String images = "";

    private String voice = "";

    private Integer id = null;

    private Integer noteId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        selectedPhotos = new ArrayList<>();
        gridViewPhotos = findViewById(R.id.gridViewPhotos);
        photoAdapter = new PhotoAdapter(this, selectedPhotos);
        gridViewPhotos.setAdapter(photoAdapter);

        // 初始化视图
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextText = findViewById(R.id.editTextText);
        editTextNewTag = findViewById(R.id.editTextNewTag);
        chipGroupTags = findViewById(R.id.chipGroupTags);

        // 获取从上一个 Activity 传递过来的笔记信息
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString("title");
            String text = extras.getString("text");
            String tags = extras.getString("tags");

            id = extras.getInt("id");
            noteId = extras.getInt("noteID");
            images = extras.getString("images");
            voice = extras.getString("voice");
            voiceFilePath = extras.getString("voice");

            // 在 EditText 中显示笔记信息
            editTextTitle.setText(title);
            editTextText.setText(text);

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

        // 返回按钮
        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());

        // 播放语音按钮
        Button buttonPlayVoice = findViewById(R.id.buttonPlayVoice);
        buttonPlayVoice.setOnClickListener(v -> playVoice());


        // 添加标签按钮
        Button buttonAddTag = findViewById(R.id.buttonAddTag);
        buttonAddTag.setOnClickListener(v -> {
            String newTag = editTextNewTag.getText().toString().trim();
            if (!newTag.isEmpty()) {
                addChipToGroup(newTag);
                editTextNewTag.setText("");
            }
        });

        // 保存按钮
        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> saveNote());
    }

    // 显示标签的方法
    private void addChipToGroup(String tag) {
        Chip chip = new Chip(this);
        chip.setText(tag);
        chip.setCheckable(false);  // 设置为不可选择，根据需要可改为true
        chip.setCloseIconVisible(true); // 显示删除图标
        chip.setOnCloseIconClickListener(v -> chipGroupTags.removeView(chip));
        chipGroupTags.addView(chip);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                selectedPhotos.add(bitmap);
                photoAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 播放语音的方法
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

    // 保存笔记的方法
    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String text = editTextText.getText().toString().trim();

        // 获取标签
        StringBuilder tagsBuilder = new StringBuilder();
        for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupTags.getChildAt(i);
            if (tagsBuilder.length() > 0) {
                tagsBuilder.append(",");
            }
            tagsBuilder.append(chip.getText().toString());
        }
        String tags = tagsBuilder.toString();

        // 调用后端 API 保存笔记（注意：需要异步请求和错误处理，这里简单化处理）
        // 例如：new SaveNoteTask().execute(noteId, title, text, tags, images, voiceFilePath);
        sendNote(noteId, title, text, tags);
        Toast.makeText(this, "笔记已保存", Toast.LENGTH_SHORT).show();
    }

    private void sendNote(Integer noteID, String title, String text, String tags){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url_path = "/note/change";
                String[][] requestHead = new String[0][2];
                Object[][] requestBody = new Object[2][2];
                HttpPostRequest request = new HttpPostRequest();
                try {
                    Object[] res = request.sendPostRequest(url_path+"?noteID="+noteID+"&title="+title+"&text="+text+"&tags="+tags, requestHead, requestBody);
                    res_type = (String) res[0];
                    JSONObject jsonObject = new JSONObject(res[1].toString());
                    Log.d(TAG, jsonObject.toString());
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        switch (res_type){
            case "Success":
                Toast.makeText(this, "上传成功", Toast.LENGTH_SHORT).show();
                break;
            case "ConnectException":
                Toast.makeText(this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "未知错误", Toast.LENGTH_SHORT).show();
                break;
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
