// com.example.mynotebook.api.YiyanApiService.java
package com.example.mynotebook.api;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YiyanApiService {
    public interface TagCallback {
        void onTagsGenerated(List<String> generatedTags);
        void onError(String errorMessage);
    }

    public static void getTags(String content, TagCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 替换为实际的 API 调用逻辑
                    List<String> generatedTags = getTagsFromApi(content);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onTagsGenerated(generatedTags));
                } catch (IOException | JSONException e) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
                }
            }
        }).start();
    }

    private static List<String> getTagsFromApi(String content) throws IOException, JSONException {
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonObject = new JSONObject();
        JSONArray messages = new JSONArray();

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "我给你如下的文本，请你判断描述的是[personal, work, study, health, hobbies, food, travel, finance, social, household, technology, miscellaneous]中的最贴切的那一项或者几项：“" + content + "”，你的回答应该用[]包起来，例如[study]，[hobbies,social]等.");

        messages.put(userMessage);
        jsonObject.put("messages", messages);

        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=24.fcdcc9affd4d0451ad72a564e71de795.2592000.1720184954.282335-78876239")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        // 解析响应并生成标签列表
        // 假设返回的标签在responseBody中，你需要根据实际响应格式进行解析
        List<String> generatedTags = parseTagsFromResponse(responseBody);

        return generatedTags;
    }

    private static List<String> parseTagsFromResponse(String responseBody) throws JSONException {
        JSONObject jsonObject = new JSONObject(responseBody);
        String result = jsonObject.getString("result");
        // 解析标签的逻辑
        // 根据实际的响应格式进行解析，这里仅做示例
        List<String> tags = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            String tagString = matcher.group(1);
            String[] tagArray = tagString.split(",");
            for (String tag : tagArray) {
                tags.add(tag.trim());
            }
        }
        for (int i = 0; i < tags.size(); i++) {
            tags.set(i, tags.get(i).replaceAll("\"", "").replaceAll("'", ""));
        }
        return tags;
    }
}



