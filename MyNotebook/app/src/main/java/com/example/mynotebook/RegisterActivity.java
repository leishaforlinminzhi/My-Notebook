package com.example.mynotebook;

import static android.content.ContentValues.TAG;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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


public class RegisterActivity extends AppCompatActivity {
    //控件
    private TextView edit_username;
    private TextView edit_password;
    private Button btn_register;

    //全局变量
    private String res_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();
        initEvent();
    }

    public void initView(){
        edit_username = this.findViewById(R.id.edit_username);
        edit_password = this.findViewById(R.id.edit_password);
        btn_register = this.findViewById(R.id.btn_register);
    }

    public void initEvent(){
        //给登录按钮添加点击事件(登录)
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户名和密码
                res_type = "";
                String username = edit_username.getText().toString();
                String password = edit_password.getText().toString();
                if (username.length()==0) {
                    Toast.makeText(RegisterActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length()==0) {
                    Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url_path = "/user/save";
                        String[][] requestHead = new String[0][2];
                        Object[][] requestBody = new Object[2][2];
                        HttpPostRequest request = new HttpPostRequest();
                        try {
                            Object[] res = request.sendPostRequest(url_path+"?username="+username+"&password="+password, requestHead, requestBody);
                            res_type = (String) res[0];
                            JSONObject jsonObject = new JSONObject(res[1].toString());
                            if (jsonObject.getInt("id") == -1){
                                res_type = "DuplicateUsername";
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
                switch (res_type){
                    case "Success":
                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        try {
                            sleep(1500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        finish();
                        break;
                    case "DuplicateUsername":
                        Toast.makeText(RegisterActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                        break;
                    case "ConnectException":
                        Toast.makeText(RegisterActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(RegisterActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

}