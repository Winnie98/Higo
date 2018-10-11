package com.yhl.higo.ec.main.personal.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.util.log.HigoLogger;

import butterknife.OnClick;

/**
 * Created by Administrator on 2018/9/2/002.
 */

public class UseExamineDelegate extends HigoDelegate {

    @OnClick(R2.id.icon_user_examine_back)
    void onClickIdentityBack(){
        getSupportDelegate().pop();
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_user_examine;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {

    }
}
