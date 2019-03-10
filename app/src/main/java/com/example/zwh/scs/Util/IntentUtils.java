package com.example.zwh.scs.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class IntentUtils {
    public static void SetIntent(Context context, Class<?> mClass) {
        Intent intent = new Intent();
        intent.setClass(context, mClass);
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
