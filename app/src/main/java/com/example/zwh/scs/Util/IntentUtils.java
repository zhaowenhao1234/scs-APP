package com.example.zwh.scs.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class IntentUtils {
    public static void SetIntent(Context context, Class<?> mClass) {

        /*
        Context中有一个startActivity方法，Activity继承自Context，重载了startActivity方法。
        如果使用 Activity的startActivity方法，不会有任何限制，
        而如果使用Context的startActivity方法的话，就需要开启一个新 的task，
        遇到上面那个异常的，都是因为使用了Context的startActivity方法。解决办法是，加一个flag。
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
         */

        Intent intent = new Intent();
        intent.setClass(context, mClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void SetIntentfinish(Context context, Class<?> mClass, boolean isclos) {
        Intent intent = new Intent();
        context.startActivity(intent);
        if (isclos) {
            ((Activity) context).finish();
        }
    }

    public static void SetIntentBundle(Context context, Class<?> mClass, Bundle bundle) {
        Intent intent = new Intent();
        intent.putExtra("bundle", bundle);
        context.startActivity(intent);
    }

    public static String handleAlipayUpperCase(String string) {
        int lastIndex = string.lastIndexOf("/");
        return "https://qr.alipay.com" + string.substring(lastIndex);
    }

}
