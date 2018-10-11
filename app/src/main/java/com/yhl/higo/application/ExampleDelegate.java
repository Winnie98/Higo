package com.yhl.higo.application;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.IError;
import com.yhl.higo.net.callback.IFailure;
import com.yhl.higo.net.callback.ISuccess;


public class ExampleDelegate extends HigoDelegate {
    @Override
    public Object setLayout() {
        return R.layout.delegate_example;
    }

    //对每一个控件所做的操作
    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        testRestCilent();

    }

    private void testRestCilent(){
        RestClient.builder()
                .url("http:127.0.0.1/index")
//                .url("http://news.baidu.com/")
//                .url("http:higo.party/order/create.do")
//                .params("shippingId",5)
//                .url("https://blog.csdn.net/liuhongwei123888/article/details/50375283")
                .loader(getContext())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d("HAHAHAHA",response);
                        Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
                    }

                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {

                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {

                    }
                })
                .build()
                .get();
    }
}
