package com.example.mynotebook;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

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
    private boolean password_currect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView(); //初始化控件
        initEvent(); //初始化事件
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
                String username = edit_username.getText().toString();
                String password = edit_password.getText().toString();
                String url_path = "http://183.172.134.224:8080/user/login";
                //调用API验证用户名密码是否正确
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String[][] requestHead = new String[0][2];
                        Object[][] requestBody = new Object[2][2];

                        HttpPostRequest request = new HttpPostRequest();
                        try {
                            Object[] res = request.sendPostRequest(url_path+"?username="+username+"&password="+password, requestHead, requestBody);
                            Log.d(TAG, res[0].toString());
                            int intValue = (int) res[0];
                            if (intValue == 200){
                                password_currect = true;
                            }
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
//                登录事件
                if(password_currect) {
                    Toast.makeText(MainActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}