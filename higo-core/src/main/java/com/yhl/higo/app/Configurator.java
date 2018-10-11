package com.yhl.higo.app;

import android.app.Activity;
import android.os.Handler;

import com.blankj.utilcode.util.Utils;
import com.joanzapata.iconify.IconFontDescriptor;
import com.joanzapata.iconify.Iconify;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Interceptor;

public class Configurator {

    private final HashMap<Object,Object> HIGO_CONFIGS=new HashMap<>();
    private static final Handler HANDLER = new Handler();
    private static final ArrayList<IconFontDescriptor> ICONS =new ArrayList<>();
    private static final ArrayList<Interceptor> INTERCEPTORS = new ArrayList<>();

    private Configurator() {
        HIGO_CONFIGS.put(ConfigKeys.CONFIG_READY.name(),false);
        HIGO_CONFIGS.put(ConfigKeys.HANDLER, HANDLER);
    }

    public static Configurator getInstance(){
        return Holder.INSTANCE;
    }

    final HashMap<Object,Object> getHigoConfigs(){
        return HIGO_CONFIGS;
    }

    private static class Holder{
        private static final Configurator INSTANCE=new Configurator();
    }

    public final void Configure(){
        initIcons();
        Logger.addLogAdapter(new AndroidLogAdapter());
        HIGO_CONFIGS.put(ConfigKeys.CONFIG_READY.name(),true);
        Utils.init(Higo.getApplicationContext());
    }

    public final Configurator withApiHost(String host){
        HIGO_CONFIGS.put(ConfigKeys.API_HOST,host);
        return this;
    }

    public final Configurator withLoaderDelayed(long delayed) {
        HIGO_CONFIGS.put(ConfigKeys.LOADER_DELAYED, delayed);
        return this;
    }

    private void initIcons(){
        if (ICONS.size()>0){
            final Iconify.IconifyInitializer initializer=Iconify.with(ICONS.get(0));
            for (int i=1;i<ICONS.size();i++){
                initializer.with(ICONS.get(i));
            }
        }
    }

    public final Configurator withIcon(IconFontDescriptor descriptor){
        ICONS.add(descriptor);
        return this;
    }

    public final Configurator withInterceptor(Interceptor interceptor){
        INTERCEPTORS.add(interceptor);
        HIGO_CONFIGS.put(ConfigKeys.INTERCEPTOR,INTERCEPTORS);
        return this;
    }

    public final Configurator withInterceptors(ArrayList<Interceptor> interceptors){
        INTERCEPTORS.addAll(interceptors);
        HIGO_CONFIGS.put(ConfigKeys.INTERCEPTOR,INTERCEPTORS);
        return this;
    }


    public final Configurator withActivity(Activity activity){
        HIGO_CONFIGS.put(ConfigKeys.ACTIVITY,activity);
        return this;
    }

    private void checkConfiguration(){
        final boolean isReady= (boolean) HIGO_CONFIGS.get(ConfigKeys.CONFIG_READY.name());
        if (!isReady){
            throw new RuntimeException("Configuration is not ready,call configure");
        }
    }


    /*
    @SuppressWarnings("unchecked")
    final <T> T getConfiguration(Object key){
        checkConfiguration();
        return (T) LATTE_CONFIGS.get(key);
    }
    */

    /*
    @SuppressWarnings("unchecked")
    final <T> T getConfiguration(Enum<ConfigKeys> key){
        checkConfiguration();
        return (T) LATTE_CONFIGS.get(key.name());
    }*/

    @SuppressWarnings("unchecked")
    final <T> T getConfiguration(Object key) {
        checkConfiguration();
        final Object value = HIGO_CONFIGS.get(key);
        if (value == null) {
            throw new NullPointerException(key.toString() + " IS NULL");
        }
        return (T) HIGO_CONFIGS.get(key);
    }


}
