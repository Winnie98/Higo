package com.yhl.higo.app;

import android.content.Context;
import android.os.Handler;

import java.util.HashMap;

public final class Higo {
    public static Configurator init(Context context){
//        getConfigurations().put(ConfigKeys.APPLICATION_CONTEXT.name(),context.getApplicationContext());
        Configurator.getInstance()
                .getHigoConfigs()
                .put(ConfigKeys.APPLICATION_CONTEXT,context.getApplicationContext());
        return Configurator.getInstance();
    }


    public static HashMap<Object,Object> getConfigurations(){
        return Configurator.getInstance().getHigoConfigs();
    }


    /*
    public static Context getApplication(){
        return (Context) getConfigurations().get(ConfigKeys.APPLICATION_CONTEXT.name());
    }
    */


    public static Configurator getConfigurator() {
        return Configurator.getInstance();
    }


    public static <T> T getConfiguration(Object key) {
//        return getConfigurator().getConfiguration((Enum<ConfigKeys>) key);
        return Configurator.getInstance().getConfiguration(key);
    }

    public static Context getApplicationContext() {
//        return getConfiguration(ConfigKeys.APPLICATION_CONTEXT);
        return Configurator.getInstance()
                .getConfiguration(ConfigKeys.APPLICATION_CONTEXT);
    }

    public static Handler getHandler() {
        return getConfiguration(ConfigKeys.HANDLER);
    }


}
