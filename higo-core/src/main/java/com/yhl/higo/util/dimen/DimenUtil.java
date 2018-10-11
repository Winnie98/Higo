package com.yhl.higo.util.dimen;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.yhl.higo.app.Higo;


public class DimenUtil {

    public static int getScreenWidth(){
        final Resources resources = Higo.getApplicationContext().getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(){
        final Resources resources = Higo.getApplicationContext().getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }
}
