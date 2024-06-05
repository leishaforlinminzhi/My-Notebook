package com.example.mynotebook.utils;

import static android.content.ContentValues.TAG;
import static java.sql.Types.NULL;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class HttpPostRequest {
    public Object[] sendPostRequest(String url, String[][] requestHead, Object[][] requestBody) throws UnsupportedEncodingException, MalformedURLException {
        Object[] res = new Object[2];
        res[1] = NULL;
        try {
            url = "http://183.172.231.132:8080" + url;
            URL path = new URL(url);
            HttpURLConnection con = (HttpURLConnection) path.openConnection();

            con.setRequestMethod("POST");//请求post方式
            con.setUseCaches(false); // Post请求不能使用缓存
            con.setDoInput(true);// 设置是否从HttpURLConnection输入，默认值为 true
            con.setDoOutput(true);// 设置是否使用HttpURLConnection进行输出，默认值为 false

            //设置header内的参数 connection.setRequestProperty("健, "值");
            for (int i = 0; i < requestHead.length; i++){
                con.setRequestProperty(requestHead[i][0], requestHead[i][1]);
            }

            //设置body内的参数，put到JSONObject中

//            JSONObject param = new JSONObject();
//            for (int i = 0; i < requestBody.length; i++){
//                param.put(((String) requestBody[i][0]),requestBody[i][1]);
//            }
//            Log.d(TAG, param.toString());

//            String rB = "username=linminzhi&password=030603lmz";
//            byte[] param = rB.getBytes(StandardCharsets.UTF_8);
//
//            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(),"UTF-8");
//            writer.write(param.toString());
//            writer.flush();

            // 获取服务端响应，通过输入流来读取URL的响应
            InputStream is = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sbf = new StringBuffer();
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();

            // 关闭连接
            con.disconnect();

            // 打印读到的响应结果
            System.out.println("运行结束："+sbf.toString());
            res[0] = "Success";
            res[1] = sbf;
            return res;

        }catch (MalformedURLException e) {
            e.printStackTrace();
            res[0] = -1;
        }catch (IOException e) {
            e.printStackTrace();
            if (e instanceof FileNotFoundException) {
                res[0] = "FileNotFoundException";
            } else if (e instanceof ConnectException) {
                res[0] = "ConnectException";
            } else {
                res[0] = "Unkown";
            }
        }
        return res;
    }
}
