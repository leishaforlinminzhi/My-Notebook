package com.example.mynotebook.fragment;

import static android.content.ContentValues.TAG;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mynotebook.GlobalValue;
import com.example.mynotebook.MainActivity;
import com.example.mynotebook.R;
import com.example.mynotebook.RegisterActivity;
import com.example.mynotebook.utils.HttpPostRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

public class mine_ChangeInfoActivity extends AppCompatActivity {

    private Integer id;
    private String res_type = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);

        GlobalValue app = (GlobalValue) getApplication();
        id = app.getId();

        // Get the intent that launched this activity, and the message in
        // the intent extra.
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        System.out.println(message);
        // Put that message into the text_message TextView.
        TextView textInfo = findViewById(R.id.tv);
        Button btn_ok = findViewById(R.id.btn_ok);
        TextView edit = findViewById(R.id.edit);

        switch (message){
            case "password":
                textInfo.setText("请输入新的密码");
                break;
            case "name":
                textInfo.setText("请输入新的用户名");
                break;
            case "signature":
                textInfo.setText("请输入新的签名");
                break;
            default:
                break;
        }

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                res_type = "";
                String info = edit.getText().toString();
                String type = message;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url_path = "/user/update";
                        String[][] requestHead = new String[0][2];
                        Object[][] requestBody = new Object[2][2];

                        HttpPostRequest request = new HttpPostRequest();
                        try {
                            Object[] res = request.sendPostRequest(url_path+"?id="+id+"&type="+type+"&info="+info, requestHead, requestBody);
                            Log.d(TAG, res[0].toString());
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
                        Toast.makeText(mine_ChangeInfoActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        try {
                            sleep(1500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        finish();
                        break;
                    case "ConnectException":
                        Toast.makeText(mine_ChangeInfoActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(mine_ChangeInfoActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}
