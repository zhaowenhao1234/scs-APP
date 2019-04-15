package com.example.zwh.scs.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.zwh.scs.Activity.MainActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * created at 2019/3/20 17:06 by wenhaoz
 */
public class UserInfoUtil {
    public static SharedPreferences userInfo;
    /***
     *保存当前用户信息到sharepreference
     *@return void
     *@author wenhaoz
     *created at 2019/3/20 16:59
     */
    public static void saveCurrentInfo(Context context, Bundle bundle) {
        userInfo = context.getSharedPreferences("uesrinfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putString("id", bundle.getString("id"));//id
        editor.putString("account", bundle.getString("account"));//账号
        editor.putString("password", bundle.getString("password"));//密码
        editor.putInt("option", bundle.getInt("option"));//用户模式（司机，乘客）
        editor.putBoolean("status", bundle.getBoolean("status"));
        editor.commit();
    }

    public static Bundle getCurrentInfoUserName(Context context) {
        userInfo = context.getSharedPreferences("uesrinfo", MODE_PRIVATE);
        String account = userInfo.getString("account", null);
        String password = userInfo.getString("account", null);
        int option = userInfo.getInt("account", 0);
        Bundle bundle = new Bundle();
        bundle.putString("account", account);
        bundle.putString("password", password);
        bundle.putInt("option", option);
        return bundle;
    }

    public static void setCurrentInfoUserState(Context context, boolean status) {
        userInfo = context.getSharedPreferences("uesrinfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putBoolean("status", status);
        editor.commit();
    }

    public static boolean getCurrentInfoUserState(Context context){
        userInfo = context.getSharedPreferences("uesrinfo",MODE_PRIVATE);
        boolean islogin = userInfo.getBoolean("status", false);
        return islogin;
    }

    public static int getCurrentInfoUserMode(Context context) {
        userInfo = context.getSharedPreferences("uesrinfo", MODE_PRIVATE);
        int option = userInfo.getInt("option", 0);
        return option;
    }

    public static String getCurrentInfoUserID(Context context) {
        userInfo = context.getSharedPreferences("uesrinfo", MODE_PRIVATE);
        String id = userInfo.getString("id", null);
        return id;
    }

    public static void saveCurrentMessage(Context context, Bundle bundle) {
        userInfo = context.getSharedPreferences("uesrinfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putString("nickName", bundle.getString("nickName"));
        editor.putInt("sex", bundle.getInt("sex"));
        editor.putString("grade", bundle.getString("grade"));
        editor.putString("schoolId", bundle.getString("schoolId"));
        editor.commit();
    }

    public static Bundle getCurrentMessage(Context context) {
        userInfo = context.getSharedPreferences("uesrinfo", MODE_PRIVATE);
        Bundle bundle = new Bundle();
        bundle.putString("nickName", userInfo.getString("nickName", null));
        bundle.putInt("sex", userInfo.getInt("sex", 0));
        bundle.putString("grade", userInfo.getString("grade", null));
        bundle.putString("schoolId", userInfo.getString("schoolId", null));
        return bundle;
    }

    public static String getUserName(Context context) {
        userInfo = context.getSharedPreferences("uesrinfo", MODE_PRIVATE);
        return userInfo.getString("nickName", null);
    }

}
