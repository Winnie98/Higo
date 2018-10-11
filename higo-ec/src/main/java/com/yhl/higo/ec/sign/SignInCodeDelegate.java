package com.yhl.higo.ec.sign;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.identify.IdentifyDelegate;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.util.CountDownTimerUtils;
import com.yhl.higo.util.log.HigoLogger;

import butterknife.BindView;
import butterknife.OnClick;

//import com.yhl.higo.wechat.LatteWeChat;
//import com.yhl.higo.wechat.callbacks.IWeChatSignInCallback;

/**
 * Created by Administrator on 2018/4/26/026.
 */

public class SignInCodeDelegate extends HigoDelegate {

    @BindView(R2.id.edit_sign_in_code_phone)
    TextInputEditText mPhone = null;
    @BindView(R2.id.edit_sign_in_code)
    TextInputEditText mCode = null;
    @BindView(R2.id.txt_get_sign_in_code)
    TextView mGetTxt = null;

    @OnClick(R2.id.txt_get_sign_in_code)
    void onClickGetCode(){
        CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(mGetTxt, 60000, 1000);
        mCountDownTimerUtils.start();

        RestClient.builder()
                .url("user/code/send_code.do")
                .params("phone",mPhone.getText().toString())
                .params("type",IdentifyItemType.IDENTIFY_SIGN_IN)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("SEND_CODE",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        final String msg  = JSON.parseObject(response).getString("msg");
                        switch (status){
                            case 0:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })
                .build()
                .post();
    }

    @OnClick(R2.id.btn_sign_in_code)
    void onClickSignInCode(){
        if (checkForm()) {

            RestClient.builder()
                    .url("user/user/login.do")
                    .params("phone", mPhone.getText().toString())
                    .params("code", mCode.getText().toString())
                    .params("password","")
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            HigoLogger.json("USER_PROFILE", response);
                            final int status = JSON.parseObject(response).getInteger("status");
                            switch (status){
                                case 0:
                                    SignHandler.onSignIn(response, mISignListener);
                                    getSupportDelegate().pop();
                                    break;
                                case 1:
                                    final String msg = JSON.parseObject(response).getString("msg");
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    break;
                            }
                        }
                    })
                    .build()
                    .post();
        }
    }

    @OnClick(R2.id.tv_link_sign_in)
    void onClickSignIn() {
        getSupportDelegate().start(new SignInDelegate());
    }


//    private String mPhoneNum;
//    private boolean mIsToIndex = false;
    private ISignListener mISignListener = null;

    public static SignInCodeDelegate create(){
        return new SignInCodeDelegate();
    }
    public static SignInCodeDelegate create(String phone){
        final Bundle args = new Bundle();
        args.putString("phone",phone);
        final SignInCodeDelegate delegate = new SignInCodeDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    public static SignInCodeDelegate create(boolean isToIndex){
        final Bundle args = new Bundle();
        args.putBoolean("isToIndex",isToIndex);
        final SignInCodeDelegate delegate = new SignInCodeDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ISignListener){
            mISignListener = (ISignListener) activity;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //android 软键盘在全屏下和沉浸式状态下EditText被覆盖解决
//        AndroidBug5497Workaround.assistActivity(getActivity());

//        final Bundle args = getArguments();
//        if (args != null) {
//            mPhoneNum = args.getString("phone");
//            mIsToIndex = args.getBoolean("isToIndex");
////            final String phone = args.getString("phone");
////            mPhone.setText(phone);
//        }
    }



    private boolean checkForm(){
        final String phone =mPhone.getText().toString();
        final String code =mCode.getText().toString();

        boolean isPass = true;//是否通过

        if (phone.isEmpty()||phone.length()!=11){
            mPhone.setError("手机号码错误");
            isPass = false;
        }else {
            mPhone.setError(null);
        }

        if (code.isEmpty()||code.length()!=6){
            mCode.setError("请填写6位数验证码");
            isPass = false;
        }else {
            mCode.setError(null);
        }
        return isPass;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_sign_in_code;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

    }
}
