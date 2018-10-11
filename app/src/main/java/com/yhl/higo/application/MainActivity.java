package com.yhl.higo.application;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.widget.Toast;

import com.yhl.higo.activities.ProxyActivity;
import com.yhl.higo.app.Higo;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.detail.order.OrderDetailDelegate;
import com.yhl.higo.ec.launcher.LauncherDelegate;
import com.yhl.higo.ec.main.EcBottomDelegate;
import com.yhl.higo.ec.main.personal.PersonalDelegate;
import com.yhl.higo.ec.sign.ISignListener;
import com.yhl.higo.ec.sign.SignInDelegate;
import com.yhl.higo.util.AndroidBug5497Workaround;
//import com.yhl.higo.util.AndroidBug5497Workaround;

import qiu.niorgai.StatusBarCompat;

import static com.yhl.higo.application.R.color.app_main;

public class MainActivity extends ProxyActivity implements
        ISignListener {
//        ILauncherListener {

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //android 软键盘在全屏下和沉浸式状态下EditText被覆盖解决
        AndroidBug5497Workaround.assistActivity(this);

        final ActionBar actionBar  = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Higo.getConfigurator().withActivity(this);
//        StatusBarCompat.translucentStatusBar(this,true);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.app_main));

    }

    @Override
    public HigoDelegate setRootDelegate() {

        return new LauncherDelegate();
    }

    @Override
    public void onSignInSuccess() {
        Toast.makeText(this,"登录成功",Toast.LENGTH_LONG).show();

        getSupportDelegate().pop();

    }

    @Override
    public void onSignUpSuccess() {
        Toast.makeText(this,"注册成功",Toast.LENGTH_LONG).show();

        getSupportDelegate().pop();
    }

}
