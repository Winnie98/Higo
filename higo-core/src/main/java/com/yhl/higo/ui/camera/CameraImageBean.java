package com.yhl.higo.ui.camera;

import android.net.Uri;

/**
 * Created by Administrator on 2018/5/25/025.
 * 存储一些中间值
 */

public final class CameraImageBean {

    private Uri  mPath = null;

    private static final CameraImageBean INSTANCE = new CameraImageBean();

    public static CameraImageBean getInstance(){
        return INSTANCE;
    }

    public Uri getPath() {
        return mPath;
    }

    public void setPath(Uri mPath) {
        this.mPath = mPath;
    }


}
