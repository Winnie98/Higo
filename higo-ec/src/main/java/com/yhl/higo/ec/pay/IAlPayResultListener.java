package com.yhl.higo.ec.pay;

/**
 * Created by Administrator on 2018/5/24/024.
 */

public interface IAlPayResultListener {

    void onPaySuccess();

    void onPaying();

    void onPayFail();

    void onPayCancel();

    void onPayConnectError();
}
