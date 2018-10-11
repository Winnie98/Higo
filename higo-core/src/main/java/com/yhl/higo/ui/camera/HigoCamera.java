package com.yhl.higo.ui.camera;

import android.net.Uri;

import com.yhl.higo.delegates.PermissionCheckerDelegate;
import com.yhl.higo.util.file.FileUtil;

/**
 * Created by Administrator on 2018/5/25/025.
 * 照相机调用类
 */


public class HigoCamera {

    public static Uri createCropFile(){
        return Uri.parse
                (FileUtil.createFile("crop_image",
                        FileUtil.getFileNameByTime("IMG","jpg")).getPath());
    }

    public static void start(PermissionCheckerDelegate delegate){
        new CameraHandler(delegate).beginCameraDialog();
    }
}
