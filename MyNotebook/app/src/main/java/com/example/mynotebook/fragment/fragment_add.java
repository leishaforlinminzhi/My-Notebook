package com.example.mynotebook.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.mynotebook.GlobalValue;
import com.example.mynotebook.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

public class fragment_add extends Fragment {
    private Integer id = null;
    private static final int REQUEST_CODE_SELECT_PHOTOS = 1;
    private EditText input_tag ;
    private GridView gridViewPhotos;
    private Button btn_addphotos;
    private Button btn_addvoice;
    private Button btn_addtag;
    private ChipGroup chipGroup;
    private List<Uri> selectedPhotos;
    private com.example.mynotebook.fragment.PhotoAdapter photoAdapter;
    private ArrayList<String> selectedChipTexts = new ArrayList<>();
    public ArrayList<String> getSelectedChipTexts() {
        return selectedChipTexts;
    }
    private MediaRecorder recorder;
    private String fileName = null;
    private boolean voiceRecording = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        GlobalValue app = (GlobalValue) requireActivity().getApplication();;
        id = app.getId();

        input_tag = view.findViewById(R.id.edit_tag);

        gridViewPhotos = view.findViewById(R.id.gridViewPhotos);
        btn_addphotos = view.findViewById(R.id.btnAddPhotos);
        btn_addvoice = view.findViewById(R.id.btnAddVoice);
        btn_addtag = view.findViewById(R.id.btnAddTag);
        chipGroup = view.findViewById(R.id.chipGroup);

        selectedPhotos = new ArrayList<>();
        photoAdapter = new com.example.mynotebook.fragment.PhotoAdapter(requireContext(), selectedPhotos);
        gridViewPhotos.setAdapter(photoAdapter);

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
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTOS);
            }
        });

        btn_addtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_tag.setVisibility(View.VISIBLE);
            }
        });

        btn_addvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(!voiceRecording) {
                        checkPermission();
                        fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
                        recorder = new MediaRecorder();
                        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorder.setOutputFile(fileName);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                        try {
                            recorder.prepare();
                            recorder.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getActivity(), "Recording started", Toast.LENGTH_SHORT).show();
                        btn_addvoice.setText("停止录音");
                        voiceRecording = true;
                    }else{
                        if (recorder != null) {
                            recorder.stop();
                            recorder.release();
                            recorder = null;
                            Toast.makeText(getActivity(), "Recording stopped", Toast.LENGTH_SHORT).show();
                        }
                        btn_addvoice.setText("已添加录音");
                    }
            }
        });

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    String chipText = chip.getText().toString();
                    Toast.makeText(getContext(), "Selected Chip: " + chipText, Toast.LENGTH_SHORT).show();
                }
            }
        });

        input_tag.setImeOptions(EditorInfo.IME_ACTION_DONE);
        input_tag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                Log.d(TAG, "here");
                String chipText = input_tag.getText().toString();
                Chip chip = new Chip(requireContext());
                chip.setText(chipText);
                chip.setCheckable(true);

                chipGroup.addView(chip);
                selectedChipTexts.add(chipText);
                input_tag.setText("");
                input_tag.setVisibility(View.INVISIBLE);

                return true;
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
                    Log.d(TAG, selectedPhoto.toString());
                } else if (data.getClipData() != null) {
                    // 多张照片
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri selectedPhoto = data.getClipData().getItemAt(i).getUri();
                        selectedPhotos.add(selectedPhoto);
                        Log.d(TAG, selectedPhoto.toString());
                    }
                }
                photoAdapter.notifyDataSetChanged();
            }
        }else if(resultCode == RESULT_OK && requestCode == 200) checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), permissions, 200);
                    return;
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode == 200) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 200);
                    return;
                }
            }
        }
    }

}
