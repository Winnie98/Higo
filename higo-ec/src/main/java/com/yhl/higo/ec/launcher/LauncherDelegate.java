package com.yhl.higo.ec.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.TextView;

import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.main.EcBottomDelegate;
import com.yhl.higo.ec.sign.SignInDelegate;
import com.yhl.higo.ui.launcher.ILauncherListener;
import com.yhl.higo.ui.launcher.ScrollLauncherTag;
import com.yhl.higo.util.storage.HigoPreference;
import com.yhl.higo.util.timer.BaseTimerTask;
import com.yhl.higo.util.timer.ITimerListener;

import java.text.MessageFormat;
import java.util.Timer;

import butterknife.BindView;
import butterknife.OnClick;


public class LauncherDelegate extends HigoDelegate implements ITimerListener{

    @BindView(R2.id.tv_launcher_timer)
    TextView mTvTimer = null;

    private Timer mTimer = null;
    private int mCount = 5;
    private ILauncherListener mILauncherListener = null;

    @OnClick(R2.id.tv_launcher_timer)
    void onClickTimerView(){
        if (mTimer!=null){
            mTimer.cancel();
            mTimer = null;
//            checkIsShowScroll();
            getSupportDelegate().replaceFragment(new EcBottomDelegate(),false);
        }
    }

    private void initTimer(){
        mTimer = new Timer();
        final BaseTimerTask task = new BaseTimerTask(this);
        mTimer.schedule(task,0,1000);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ILauncherListener){
            mILauncherListener = (ILauncherListener) activity;
        }
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_launcher;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initTimer();
    }



    @Override
    public void onTimer() {
        getProxyActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mTvTimer!=null){
                    mTvTimer.setText(MessageFormat.format("跳过\n{0}s",mCount));
                    mCount--;
                    if (mCount<0){
                        if (mTimer!=null){
                            mTimer.cancel();
                            mTimer = null;
//                            checkIsShowScroll();
                            getSupportDelegate().replaceFragment(new EcBottomDelegate(),false);
                        }
                    }
                }
            }
        });
    }
}
