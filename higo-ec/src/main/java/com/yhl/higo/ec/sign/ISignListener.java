package com.yhl.higo.ec.sign;

/**
 * Created by Administrator on 2018/5/2/002.
 */

public interface ISignListener {
    //登录成功的回调
    void onSignInSuccess();

    //注册成功的回调
    void onSignUpSuccess();

}
