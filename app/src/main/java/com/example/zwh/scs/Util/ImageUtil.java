package com.example.zwh.scs.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.example.zwh.scs.R;

/**
 * created at 2019/3/9 13:34 by wenhaoz
 */
public class ImageUtil {

    public static BitmapDescriptor setImage(Context context, int id, float x, float y) {
        Matrix matrix = new Matrix();
        matrix.setScale(x, y);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), id);
        Bitmap mSrcBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(mSrcBitmap);
        return bitmapDescriptor;
    }

}
