package com.yhl.higo.net.rx;

import android.content.Context;

import com.yhl.higo.net.RestCreator;
import com.yhl.higo.ui.loader.LoaderStyle;

import java.io.File;
import java.util.Map;
import java.util.WeakHashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RxRestCilentBuilder {

    private String mUrl = null;
//    private Map<String,Object> mParams;
    private static final Map<String,Object> PARAMS = RestCreator.getParams();
    private RequestBody mBody = null;
    private Context mContext = null;
    private LoaderStyle mLoaderStyle = null;
    private File mFile = null;

    RxRestCilentBuilder(){

    }

    public final RxRestCilentBuilder url(String url){
        this.mUrl=url;
        return this;
    }

    public final RxRestCilentBuilder params(WeakHashMap<String,Object> params){
//        this.mParams=params;
        PARAMS.putAll(params);
        return this;
    }

    public final RxRestCilentBuilder params(String key, Object value){
//        if (mParams == null){
//            mParams = new WeakHashMap<>();
//        }
        //this.mParams.put(key, value);
        PARAMS.put(key, value);
        return this;
    }

    public final RxRestCilentBuilder file(File file){
        this.mFile = file;
        return this;
    }

    public final RxRestCilentBuilder file(String file){
        this.mFile = new File(file);
        return this;
    }

    public final RxRestCilentBuilder raw(String raw){
        this.mBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"),raw);
        return this;
    }

    public final RxRestCilentBuilder loader(Context context, LoaderStyle style){
        this.mContext=context;
        this.mLoaderStyle=style;
        return this;
    }

    public final RxRestCilentBuilder loader(Context context){
        this.mContext=context;
        this.mLoaderStyle=LoaderStyle.BallClipRotatePulseIndicator;
        return this;
    }

    public final RxRestClient build(){
        return new RxRestClient(mUrl,PARAMS,mBody,mFile,mContext,mLoaderStyle);
    }
}
