package com.example.mynotebook;


import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynotebook.utils.HttpGetRequest;
import com.example.mynotebook.utils.HttpPostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;


public class MainActivity extends AppCompatActivity {
    //控件
    private TextView edit_username;
    private TextView edit_password;
    private Button btn_login;

    //全局变量
    private boolean sign_login = false;
    private int id = -1;
    private String res_type = "";
    public static final String EXTRA_MESSAGE = "com.example.android.mynotebook.mainactivity.extra.MESSAGE";;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    public void initView(){
        edit_username = this.findViewById(R.id.edit_username);
        edit_password = this.findViewById(R.id.edit_password);
        btn_login = this.findViewById(R.id.btn_login);
    }

    public void initEvent(){
        //给登录按钮添加点击事件(登录)
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户名和密码
                sign_login = false;
                res_type = "";
                String username = edit_username.getText().toString();
                String password = edit_password.getText().toString();
                if (username.length()==0) {
                    Toast.makeText(MainActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length()==0) {
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                //调用API验证用户名密码是否正确
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url_path = "/user/login";
                        String[][] requestHead = new String[0][2];
                        Object[][] requestBody = new Object[2][2];

                        HttpPostRequest request = new HttpPostRequest();
                        try {
                            Object[] res = request.sendPostRequest(url_path+"?username="+username+"&password="+password, requestHead, requestBody);
                            res_type = (String) res[0];
                            Log.d(TAG, res[0].toString());
                            Log.d(TAG, res[1].toString());
                            switch (res_type){
                                case "Success":
                                    JSONObject jsonObject = new JSONObject(res[1].toString());
                                    id = jsonObject.getInt("id");
                                    if (id == -1){
                                        res_type = "DuplicateUsername";
                                    }
                            }
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
                //登录事件
                switch (res_type){
                    case "Success":
                        sign_login = true;
                        Log.d(TAG, Integer.toString(id));
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        GlobalValue app = (GlobalValue) getApplication();
                        app.setId(id);
                        GotoNotebook(id);
                        break;
                    case "FileNotFoundException":
                        Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                        break;
                    case "ConnectException":
                        Toast.makeText(MainActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    public void GotoRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    public void GotoNotebook(int id) {
        Intent intent = new Intent(this, NotebookActivity.class);
        String idText = Integer.toString(id);
        intent.putExtra(EXTRA_MESSAGE, idText);
        startActivity(intent);
    }

}