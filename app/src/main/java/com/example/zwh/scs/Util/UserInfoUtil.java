package com.example.zwh.scs.Util;

import android.content.Context;
import android.content.SharedPreferences;
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
    public static void saveCurrentInfo(Context context, int option) {
        userInfo = context.getSharedPreferences("uesrinfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putInt("option",option);//用户模式（司机，乘客）
        editor.putBoolean("islogin", MainActivity.isLogin);
        editor.commit();
    }

    public static int getCurrentInfoUserName(Context context) {
        userInfo = context.getSharedPreferences("uesrinfo",MODE_PRIVATE);
        int userOption=userInfo.getInt("option",0);
        return userOption;
    }

    public static boolean getCurrentInfoUserState(Context context){
        userInfo = context.getSharedPreferences("uesrinfo",MODE_PRIVATE);
        boolean islogin=userInfo.getBoolean("islogin",false);
        return islogin;
    }

}
