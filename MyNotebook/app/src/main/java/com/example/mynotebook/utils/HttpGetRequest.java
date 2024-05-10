package com.example.mynotebook.utils;

import static java.sql.Types.NULL;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class HttpGetRequest {
    public Object[] sendGetRequest(URL url) {
        Object[] res = new Object[2];
        res[1] = NULL;
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            /// 请求方式
            conn.setRequestMethod("GET");
            /// 请求超时时长
            conn.setConnectTimeout(6000);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 5.判断响应码并获取响应数据（响应正文）
                /// 获取响应的流
                InputStream in = conn.getInputStream();
                byte[] b = new byte[1024];
                int len = 0;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                /// 在循环中读取输入流
                while ((len = in.read(b)) > -1) {// in.read()返回是int类型数据，代表实际读到的数据长度
                    // 将字节数组里面的内容写入缓存流
                    // 参数1：待写入的数组   参数2：起点  参数3：长度
                    baos.write(b, 0, len);
                }

                String msg = new String(baos.toByteArray());
                Log.e("TAG", msg );
                res[0] = 200;
                res[1] = msg;
                return res;
            }else{
                res[0] = conn.getResponseCode();
                return res;
            }
        }catch (MalformedURLException e) {
            e.printStackTrace();
            res[0] = -1;
        }catch (IOException e) {
            e.printStackTrace();
            res[0] = -2;
        }
        return res;
    }
}

