package com.yhl.higo.util.callback;

import java.util.WeakHashMap;

/**
 * Created by Administrator on 2018/5/26/026.
 */

public class CallBackManager {

    private static final WeakHashMap<Object,IGlobalCallback> CALLBACKS = new WeakHashMap<>();

    private static class Holder{
        private static final CallBackManager INSTANCE = new CallBackManager();
    }

    public static CallBackManager getInstance(){
        return Holder.INSTANCE;
    }

    public CallBackManager addCallback(Object tag, IGlobalCallback callback){
        CALLBACKS.put(tag,callback);
        return this;
    }

    public IGlobalCallback getCallback(Object tag){
        return CALLBACKS.get(tag);
    }
}
