package com.example.mynotebook.fragment;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mynotebook.GlobalValue;
import com.example.mynotebook.MainActivity;
import com.example.mynotebook.R;
import com.example.mynotebook.utils.HttpPostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;


public class fragment_mine extends Fragment {

    private Integer id = null;
    private String username = null;
    private String avatar = null;
    private String signature = null;

    private ImageView image_avatar;
    private TextView view_username;
    private TextView view_signature;
    private Button btn_info;
    private Button btn_password;

    private View view = null;

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getInfo();

        System.out.println("here");
        if (username != null)
            view_username.setText(username);
        if (signature != null)
            view_signature.setText(signature);

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_mine, container, false);

        GlobalValue app = (GlobalValue) requireActivity().getApplication();;
        id = app.getId();
        System.out.println(id);

        image_avatar = view.findViewById(R.id.avatarImageView);
        view_username = view.findViewById(R.id.usernameTextView);
        view_signature = view.findViewById(R.id.signatureTextView);
        btn_info = view.findViewById(R.id.changeInfoButton);
        btn_password = view.findViewById(R.id.changePasswordButton);

        return view;
    }
}
