package com.example.zwh.scs.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

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
    public static void saveCurrentInfo(Context context, int option, String str_username) {
        userInfo = context.getSharedPreferences("uesrinfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putString("username",str_username);//用户名
        editor.putInt("option",option);//用户模式（司机，乘客）
        editor.commit();
    }

    public static int getCurrentInfoUserName(Context context) {
        userInfo = context.getSharedPreferences("uesrinfo",MODE_PRIVATE);
        String userName=userInfo.getString("username",null);
        int userOption=userInfo.getInt("option",0);
        Log.d("dfa", "getCurrentInfoUserName: "+userOption);
        return userOption;
    }



}
