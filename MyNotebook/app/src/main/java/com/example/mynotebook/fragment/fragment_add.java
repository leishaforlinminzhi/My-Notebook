package com.example.mynotebook.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;
import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.mynotebook.GlobalValue;
import com.example.mynotebook.R;
import com.example.mynotebook.api.YiyanApiService;
import com.example.mynotebook.utils.HttpPostRequest;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;
import com.example.mynotebook.api.YiyanApiService;

public class fragment_add extends Fragment {
    private Integer id = null;
    private static final int UPLOAD_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;
    private static final int REQUEST_PERMISSION = 3;
    private EditText input_title;
    private EditText input_text;
    private EditText input_tag ;
    private GridView gridViewPhotos;
    private Button btn_addphotos;
    private Button btn_addvoice;
    private Button btn_addtag;
    private Button btn_ok;
    private ChipGroup chipGroup;
    private ConstraintLayout rootLayout;
    private ConstraintLayout Layout1;
    private ConstraintLayout Layout2;
    private ConstraintLayout Layout3;
    private List<Bitmap> selectedPhotos;
    private String voicePath;
    private com.example.mynotebook.tools.PhotoAdapter photoAdapter;
    private MediaRecorder recorder;
    private String fileName = null;
    private Integer voiceRecording = 0;
    private String res_type = "";
    private Integer noteID = 0;
    private Integer addNote = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        GlobalValue app = (GlobalValue) requireActivity().getApplication();
        ;
        id = app.getId();

        input_title = view.findViewById(R.id.edit_title);
        input_text = view.findViewById(R.id.edit_text);
        input_tag = view.findViewById(R.id.edit_tag);

        gridViewPhotos = view.findViewById(R.id.gridViewPhotos);
        btn_addphotos = view.findViewById(R.id.btnAddPhotos);
        btn_addvoice = view.findViewById(R.id.btnAddVoice);
        btn_addtag = view.findViewById(R.id.btnAddTag);
        btn_ok = view.findViewById(R.id.btnok);
        chipGroup = view.findViewById(R.id.chipGroup);

        selectedPhotos = new ArrayList<>();
        photoAdapter = new com.example.mynotebook.tools.PhotoAdapter(requireContext(), selectedPhotos);
        gridViewPhotos.setAdapter(photoAdapter);

        rootLayout = view.findViewById(R.id.constraintLayout);
        Layout1 = view.findViewById(R.id.constraintLayout1);
        Layout2 = view.findViewById(R.id.constraintLayout2);
        Layout3 = view.findViewById(R.id.constraintLayout3);

        gridViewPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 处理照片点击事件，例如查看大图或者删除照片

            }
        });

        btn_addphotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动相册选择照片
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                startActivityForResult(intent, UPLOAD_IMAGE_REQUEST);
                String[] options = {"选择照片", "拍照"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("选择图片来源");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // 从相册选择
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                startActivityForResult(pickPhoto, UPLOAD_IMAGE_REQUEST);
                                break;
                            case 1: // 使用相机拍照
                                checkPermission();
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takePicture, TAKE_PHOTO_REQUEST);
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

        btn_addtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_tag.setVisibility(View.VISIBLE);
                input_tag.requestFocus();
                addNote = 1;
            }
        });

        btn_addvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (voiceRecording) {
                    case 0:
                        checkPermission();

                        noteID = getNoteID();
                        File externalFilesDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        File directory = new File(externalFilesDir, "voice/" + Integer.toString(noteID) + "/");
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }
                        voicePath = externalFilesDir.getAbsolutePath() + "/voice/" + Integer.toString(noteID) + "/voice.3gp";

                        if (recorder == null) {
                            recorder = new MediaRecorder();
                            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            recorder.setOutputFile(voicePath);
                            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                            try {
                                recorder.prepare();
                                recorder.start();
                            } catch (IOException e) {
                                Toast.makeText(getActivity(), "麦克风开启失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "麦克风出现异常", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getActivity(), "开始录音", Toast.LENGTH_SHORT).show();
                        btn_addvoice.setText("停止录音");
                        voiceRecording = 1;
                        break;
                    case 1:
                        if (recorder != null) {
                            recorder.stop();
                            recorder.release();
                            recorder = null;
                            Toast.makeText(getActivity(), "结束录音", Toast.LENGTH_SHORT).show();
                        }
                        btn_addvoice.setText("已添语音");
                        voiceRecording = 2;
                        break;
                    case 2:
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        try {
                            Toast.makeText(getActivity(), "录音试听中", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, voicePath);
                            File file = new File(voicePath);
                            if (!file.exists()) {
                                Toast.makeText(getActivity(), "文件丢失", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mediaPlayer.setDataSource(voicePath);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    // 播放完成后的操作
                                    mediaPlayer.release();
                                }
                            });
                        } catch (IOException e) {
                            Toast.makeText(getActivity(), "试听失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO:将事件发送给后端

                int noteID = getNoteID();

                // 标题信息
                String title = input_title.getText().toString();
                if (title.length() == 0) {
                    Toast.makeText(getActivity(), "请输入标题", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    title = URLEncoder.encode(title, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                // 正文信息
                String text = input_text.getText().toString();
                if (text.length() == 0) {
                    Toast.makeText(getActivity(), "请输入正文", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    text = URLEncoder.encode(text, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                // 图片
                final String[] images = {null};
                try {
                    images[0] = URLEncoder.encode(selectedPhotos.toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                // 语音
                String voice = null;
                if (voiceRecording == 2) {
                    File externalFilesDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    voice = "voice/" + Integer.toString(noteID) + "/voice.3gp";
                }

                // tag 信息
                final ArrayList<String>[] selectedChipTexts = new ArrayList[]{new ArrayList<>()};
                for (int i = 0; i < chipGroup.getChildCount(); i++) {
                    View view = chipGroup.getChildAt(i);
                    if (view instanceof Chip) {
                        Chip chip = (Chip) view;
                        // 为每个Chip添加相同的点击监听器函数
                        if (chip.isChecked()) {
                            // 如果Chip被选中，则执行相应的逻辑
                            selectedChipTexts[0].add(chip.getText().toString());
                        }
                    }
                }

                // 调用文心一言 API 获取自动生成的标签
                String finalTitle = title;
                String finalText = text;
                String finalVoice = voice;
                YiyanApiService.getTags(text, new YiyanApiService.TagCallback() {
                    @Override
                    public void onTagsGenerated(List<String> generatedTags) {
                        // 添加大模型生成的标签
                        for (String tag : generatedTags) {
                            if (!selectedChipTexts[0].contains(tag) && tag.length() > 0) {
                                selectedChipTexts[0].add(tag);
                            }
                        }

                        String tags;
                        // 编码标签信息
                        try {
                            tags = URLEncoder.encode(selectedChipTexts[0].toString(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }

                        Log.d(TAG, finalTitle);
                        Log.d(TAG, finalText);
                        Log.d(TAG, selectedPhotos.toString());
                        Log.d(TAG, selectedChipTexts[0].toString());

                        // 都没问题之后再保存
                        if (selectedPhotos.size() > 0) {
                            try {
                                images[0] = URLEncoder.encode(saveImages(noteID).toString(), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        input_text.setText("");
                        input_title.setText("");
                        selectedPhotos.clear();
                        photoAdapter.notifyDataSetChanged();
                        voiceRecording = 0;
                        btn_addvoice.setText("开始录音");

                        // 清除 ChipGroup 中的所有 Chip，保留 Add Tag 按钮
                        for (int i = chipGroup.getChildCount() - 1; i >= 0; i--) {
                            View view = chipGroup.getChildAt(i);
                            if (view instanceof Chip && view != btn_addtag) {
                                chipGroup.removeView(view);
                            }
                        }

                        sendNote(id, noteID, finalTitle, finalText, images[0], tags, finalVoice);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(getActivity(), "标签生成失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });




        input_tag.setImeOptions(EditorInfo.IME_ACTION_DONE);
        input_tag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (input_tag.getVisibility() == View.VISIBLE) {
                    addChip();
                }
            }
        });
        input_tag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addChip();
                return true;
            }
        });
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 判断触摸点是否在 EditText 之外
                    Log.d(TAG, "tip");
                    if (isTouchOutsideView(input_tag, event)) {
                        Log.d(TAG, "here");
                        addChip();
                    }
                }
                return true;
            }
        });
        Layout1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 判断触摸点是否在 EditText 之外
                    Log.d(TAG, "tip");
                    if (isTouchOutsideView(input_tag, event)) {
                        Log.d(TAG, "here");
                        addChip();
                    }
                }
                return true;
            }
        });
        Layout2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 判断触摸点是否在 EditText 之外
                    Log.d(TAG, "tip");
                    if (isTouchOutsideView(input_tag, event)) {
                        Log.d(TAG, "here");
                        addChip();
                    }
                }
                return true;
            }
        });
        Layout3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 判断触摸点是否在 EditText 之外
                    Log.d(TAG, "tip");
                    if (isTouchOutsideView(input_tag, event)) {
                        Log.d(TAG, "here");
                        addChip();
                    }
                }
                return true;
            }
        });
        chipGroup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // 判断触摸点是否在 EditText 之外
                    Log.d(TAG, "tip");
                    if (isTouchOutsideView(input_tag, event)) {
                        Log.d(TAG, "here");
                        addChip();
                    }
                }
                return true;
            }
        });
        return view;
    }

    private boolean isTouchOutsideView(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int touchX = (int) event.getRawX();
        int touchY = (int) event.getRawY();
        return touchX < x || touchX > x + view.getWidth() || touchY < y || touchY > y + view.getHeight();
    }

    private void addChip() {
        String chipText = input_tag.getText().toString().trim();  // 使用 trim() 去除前后空格
        if (chipText.length() == 0) {
            if (addNote == 1) {
                addNote = 0;
            } else {
                input_tag.setVisibility(View.INVISIBLE);
            }
            return;
        }

        // 检查是否已经存在相同文本的 Chip
        int chipCount = chipGroup.getChildCount();
        for (int i = 1; i < chipCount; i++) {
            Chip existingChip = (Chip) chipGroup.getChildAt(i);
            if (existingChip.getText().toString().equals(chipText)) {
                input_tag.setVisibility(View.INVISIBLE);
                input_tag.setText("");
                return;  // 如果存在相同的 Chip，则返回，不添加新的 Chip
            }
        }

        input_tag.setVisibility(View.INVISIBLE);
        Chip chip = new Chip(requireContext());
        chip.setText(chipText);
        chip.setCheckable(true);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chip chip = (Chip) v;
                if (chip.isChecked()) {
                    // 如果Chip被选中，则执行相应的逻辑
                    Log.d(TAG, "Chip selected: " + chip.getText().toString());
                } else {
                    // 如果Chip被取消选中，则执行相应的逻辑
                    Log.d(TAG, "Chip deselected: " + chip.getText().toString());
                }
            }
        });

        chipGroup.addView(chip);
        input_tag.setText("");
    }


    private int getNoteID(){
        res_type = "";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url_path = "/note/getNoteID";
                String[][] requestHead = new String[0][2];
                Object[][] requestBody = new Object[2][2];
                HttpPostRequest request = new HttpPostRequest();
                try {
                    Object[] res = request.sendPostRequest(url_path, requestHead, requestBody);
                    res_type = (String) res[0];
                    Log.d(TAG, res[1].toString());
                    noteID = Integer.parseInt(res[1].toString().trim()) + 1;
                    Log.d(TAG, noteID.toString());
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                } catch (MalformedURLException e) {
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
                return noteID;
            case "ConnectException":
                Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), "未知错误", Toast.LENGTH_SHORT).show();
                break;
        }
        return -1;
    }
    private void sendNote(Integer id, Integer noteID, String title, String text, String images, String tags, String voice){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url_path = "/note/save";
                String[][] requestHead = new String[0][2];
                Object[][] requestBody = new Object[2][2];
                HttpPostRequest request = new HttpPostRequest();
                try {
                    Object[] res = request.sendPostRequest(url_path+"?id="+id+"&noteID="+noteID+"&title="+title+"&text="+text+"&images="+images+"&tags="+tags+"&voice="+voice, requestHead, requestBody);
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
                Toast.makeText(getActivity(), "上传成功", Toast.LENGTH_SHORT).show();
                break;
            case "ConnectException":
                Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), "未知错误", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private List<String> saveImages(Integer noteID){
        List<String> images = new ArrayList<>();;
        for (int i = 0; i < selectedPhotos.size(); i++) {
            String filename = "img/"+Integer.toString(noteID)+"/"+Integer.toString(i)+".png";
            saveImageToAppDirectory(noteID, selectedPhotos.get(i), filename);
            images.add(filename);
        }
        return images;
    }
    private void saveImageToAppDirectory(Integer noteID, Bitmap bitmap, String fileName) {
        File externalFilesDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File directory = new File(externalFilesDir, "img/"+Integer.toString(noteID)+"/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        if (externalFilesDir != null) {
            File imageFile = new File(externalFilesDir, fileName);
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case UPLOAD_IMAGE_REQUEST:
                if (resultCode == getActivity().RESULT_OK) {
                    // 处理选择的照片
                    if (data != null) {
                        if (data.getData() != null) {
                            // 单张照片
                            Uri selectedPhoto = data.getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedPhoto);
                                selectedPhotos.add(bitmap);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            Log.d(TAG, selectedPhoto.toString());
                        } else if (data.getClipData() != null) {
                            // 多张照片
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri selectedPhoto = data.getClipData().getItemAt(i).getUri();
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedPhoto);
                                    selectedPhotos.add(bitmap);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                Log.d(TAG, selectedPhoto.toString());
                            }
                        }
                        photoAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case TAKE_PHOTO_REQUEST:
                if (resultCode == RESULT_OK) {
                    // 获取图片
                    if (data != null && data.getExtras() != null) {
                        Bundle bundle = data.getExtras();
                        // 转换图片的二进制流
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        if (bitmap != null) {
                            // 设置图片
                            selectedPhotos.add(bitmap);
                            photoAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Bitmap is null");
                        }
                    } else {
                        Log.d(TAG, "Intent data or extras is null");
                    }

                } else {
                    Log.d(TAG, "Result code is not OK");
                }
                break;
            case REQUEST_PERMISSION:
                if(resultCode == RESULT_OK) checkPermission();
                break;
            default:
                break;
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_PERMISSION);
                    return;
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode == REQUEST_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_PERMISSION);
                    return;
                }
            }
        }
    }

}