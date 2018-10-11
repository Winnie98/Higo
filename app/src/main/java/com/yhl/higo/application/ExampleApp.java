package com.yhl.higo.application;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.yhl.higo.app.Higo;
import com.yhl.higo.ec.icon.FontEcMoudle;
import com.yhl.higo.net.interceptors.DebugInterceptor;


public class ExampleApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Higo.init(this)
                .withIcon(new FontAwesomeModule())
                .withIcon(new FontEcMoudle())
                .withLoaderDelayed(1000)
//                .withApiHost("http://127.0.0.1/")
                .withApiHost("http://higo.party/")
//                .withApiHost("http://104.199.162.204:8080/")
//                .withApiHost("http://35.201.135.118:8080/")
                .withInterceptor(new DebugInterceptor("test",R.raw.test))
                .Configure();
        initStetho();
//        DatabaseManager.getInstance().init(this);

    }

    private void initStetho(){
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build()
        );
    }
}
