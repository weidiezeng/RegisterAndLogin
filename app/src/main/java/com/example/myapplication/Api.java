package com.example.myapplication;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @Author: weidie zeng
 * @mail 1061875902@qq.com
 * @CreateDate: 2020/8/29 14:24
 * @Description:
 */
public class Api {
    //没有做后台响应，随意用了Api
    public static String HOST="https://www.baidu.com";
    public static void login(String username, String password, Callback callback){
        OkHttpClient okHttpClient=new OkHttpClient();
        RequestBody requestBody=new FormBody.Builder()
                .add("username",username)
                .add("password",password)
                .build();
        Request request=new Request.Builder()
                .url(HOST)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);

    }

}
