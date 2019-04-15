package com.example.zwh.scs.Util;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ResourceCursorAdapter;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.example.zwh.scs.Bean.Driver;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.security.PublicKey;
import java.security.cert.TrustAnchor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * created at 2019/4/13 22:26 by wenhaoz
 */
public class UploadUtil {

    public static void uploadRoute(Context context, LatLng st, LatLng en) {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("id", UserInfoUtil.getCurrentInfoUserID(context));
        formBody.add("rlLongtitude", String.valueOf(st.longitude));
        formBody.add("rlLatitude", String.valueOf(st.latitude));
        formBody.add("endLongtitude", String.valueOf(en.longitude));
        formBody.add("endLatitude", String.valueOf(en.latitude));
        String url = "http://129.204.119.172:8080/driver/update";
        Request request = new Request.Builder()//创建Request 对象。
                .url(url).post(formBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("uploadRoute", "onResponse: " + response.body().toString());
            }
        });
    }


}
