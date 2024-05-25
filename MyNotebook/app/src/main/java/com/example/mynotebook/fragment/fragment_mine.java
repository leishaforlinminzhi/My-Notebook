package com.example.mynotebook.fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import static com.example.mynotebook.MainActivity.EXTRA_MESSAGE;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.mynotebook.GlobalValue;
import com.example.mynotebook.R;
import com.example.mynotebook.utils.HttpPostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class fragment_mine extends Fragment {

    private static final int UPLOAD_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;
    private static final int INPUT_CHANGE_INFO = 3;
    private static final int REQUEST_STORAGE_PERMISSION = 4;
    private Uri photoUri;
    private String res_type = "";
    private Integer id = null;
    private String username = null;
    private String avatar = null;
    private String signature = null;
    private ImageView image_avatar;
    private TextView view_username;
    private TextView view_signature;
    private Button btn_name;
    private Button btn_signature;
    private Button btn_password;
    private Button btn_avatar;
    private Uri currentPictureUrl = null;

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, UPLOAD_IMAGE_REQUEST);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        GlobalValue app = (GlobalValue) requireActivity().getApplication();;
        id = app.getId();

        image_avatar = view.findViewById(R.id.avatarImageView);
        view_username = view.findViewById(R.id.usernameTextView);
        view_signature = view.findViewById(R.id.signatureTextView);
        btn_name = view.findViewById(R.id.changeNameButton);
        btn_signature = view.findViewById(R.id.changeSignatureButton);
        btn_password = view.findViewById(R.id.changePasswordButton);
        btn_avatar = view.findViewById(R.id.changeAvatarButton);

        return view;
    }

    private void getInfo(){
        String url_path = "/user/getById";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String[][] requestHead = new String[0][2];
                Object[][] requestBody = new Object[2][2];
                HttpPostRequest request = new HttpPostRequest();
                try {
                    Object[] res = request.sendPostRequest(url_path+"?id="+Integer.toString(id), requestHead, requestBody);
                    Log.d(TAG, res[0].toString());
                    JSONObject jsonObject = new JSONObject(res[1].toString());
                    Log.d(TAG, jsonObject.toString());
                    username = jsonObject.getString("username");
                    avatar = jsonObject.getString("avatar");
                    signature = jsonObject.getString("signature");
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
    }

    public void uploadPicture(View view) {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, UPLOAD_IMAGE_REQUEST);
        String[] options = {"选择照片", "拍照"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择图片来源");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // 从相册选择
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case UPLOAD_IMAGE_REQUEST:
                if(data != null && data.getData() != null){
                    currentPictureUrl = data.getData();
                    image_avatar.setImageURI(currentPictureUrl);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), currentPictureUrl);
                        saveImageToAppDirectory(bitmap, (Integer.toString(id)+"avatar.png"));
                        changeAvatar(Integer.toString(id)+"avatar.png");
                    } catch (IOException e) {
//                        throw new RuntimeException(e);
                        Toast.makeText(getContext(), "查找图片文件失败", Toast.LENGTH_SHORT).show();
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
                            image_avatar.setImageBitmap(bitmap);
                            saveImageToAppDirectory(bitmap, (Integer.toString(id)+"avatar.png"));
                            changeAvatar(Integer.toString(id)+"avatar.png");
                            Log.d(TAG, "success");
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
            case INPUT_CHANGE_INFO:
                onResume();
                break;
            default:
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onResume();
    }

    private void gotoChangeInfo(String type){
        Intent intent = new Intent(getActivity(), mine_ChangeInfoActivity.class);
        intent.putExtra(EXTRA_MESSAGE, type);
        startActivityForResult(intent, INPUT_CHANGE_INFO);
    }

    private void changeAvatar(String info){
        String type = "avatar";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url_path = "/user/update";
                String[][] requestHead = new String[0][2];
                Object[][] requestBody = new Object[2][2];

                HttpPostRequest request = new HttpPostRequest();
                try {
                    Object[] res = request.sendPostRequest(url_path+"?id="+id+"&type="+type+"&info="+info, requestHead, requestBody);
                    res_type = res[0].toString();
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
                Toast.makeText(getContext(), "添加成功", Toast.LENGTH_SHORT).show();
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "ConnectException":
                Toast.makeText(getContext(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getContext(), "未知错误", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        getInfo();

        File externalFilesDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir != null) {
            File imageFile = new File(externalFilesDir, Integer.toString(id)+"avatar.png");
            if (imageFile.exists()) {
                Bitmap bitmap =BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                image_avatar.setImageBitmap(bitmap);
            }
        }

        view_username.setText(username);
        view_signature.setText(signature);
        btn_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPicture(v);
            }
        });
        btn_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChangeInfo("name");
            }
        });
        btn_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChangeInfo("password");
            }
        });
        btn_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChangeInfo("signature");
            }
        });
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.CAMERA};
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_STORAGE_PERMISSION);
                    return;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode == REQUEST_STORAGE_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_STORAGE_PERMISSION);
                    return;
                }
            }
        }
    }
    private void saveImageToAppDirectory(Bitmap bitmap, String fileName) {
        File externalFilesDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

}