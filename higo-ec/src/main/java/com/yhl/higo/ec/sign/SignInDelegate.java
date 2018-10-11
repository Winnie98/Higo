package com.yhl.higo.ec.sign;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.retrieve.GetQuestionDelegate;
import com.yhl.higo.ec.save.ShareHelper;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.IError;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.util.log.HigoLogger;
//import com.yhl.higo.wechat.LatteWeChat;
//import com.yhl.higo.wechat.callbacks.IWeChatSignInCallback;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/26/026.
 */

public class SignInDelegate extends HigoDelegate {


    private ShareHelper sp;

    @BindView(R2.id.edit_sign_in_phone)
    TextInputEditText mPhone = null;
    @BindView(R2.id.edit_sign_in_password)
    TextInputEditText mPassword = null;

    private String mPhoneNum;
    private boolean mIsToIndex = false;
    private ISignListener mISignListener = null;

    public static SignInDelegate create(){
        return new SignInDelegate();
    }
    public static SignInDelegate create(String phone){
        final Bundle args = new Bundle();
        args.putString("phone",phone);
        final SignInDelegate delegate = new SignInDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    public static SignInDelegate create(boolean isToIndex){
        final Bundle args = new Bundle();
        args.putBoolean("isToIndex",isToIndex);
        final SignInDelegate delegate = new SignInDelegate();
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

        final Bundle args = getArguments();
        if (args != null) {
            mPhoneNum = args.getString("phone");
            mIsToIndex = args.getBoolean("isToIndex");
//            final String phone = args.getString("phone");
//            mPhone.setText(phone);
        }
        sp=new ShareHelper(getContext());
    }

    @OnClick(R2.id.btn_sign_in)
    void onClickSignIn() {
        if (checkForm()) {
            //保存用户名和密码到共享文件中
            sp.save(mPhone.getText().toString(),mPassword.getText().toString());

            RestClient.builder()
                    .url("user/user/login.do")
                    .params("phone", mPhone.getText().toString())
                    .params("password", mPassword.getText().toString())
                    .params("code","")
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
                    .error(new IError() {
                        @Override
                        public void onError(int code, String msg) {
                            HigoLogger.d("SIGN_IN_ERROR","code:"+code+"msg:"+msg);
                        }
                    })
                    .build()
                    .post();
        }
    }

    @OnClick(R2.id.tv_link_sign_in_code)
    void onClickSignInCode(){
        getSupportDelegate().pop();
    }

//    @OnClick(R2.id.tv_link_sign_up)
//    void onClickLinkSignUp(){
////        getSupportDelegate().start(IdentifyDelegate.create(IdentifyItemType.IDENTIFY_SIGN_UP));
//        getSupportDelegate().start(new SignUpDelegate());
//    }

    @OnClick(R2.id.tv_forget_pwd)
    void onClickForgetPwd(){
        getSupportDelegate().start(new GetQuestionDelegate());
//        getSupportDelegate().start(IdentifyDelegate.create(IdentifyItemType.IDENTIFY_RESET_PWD));
    }

    private boolean checkForm(){
        final String phone =mPhone.getText().toString();
        final String password =mPassword.getText().toString();

        boolean isPass = true;//是否通过

        if (phone.isEmpty()||phone.length()!=11){
            mPhone.setError("手机号码错误");
            isPass = false;
        }else {
            mPhone.setError(null);
        }

        if (password.isEmpty()||password.length()<6){
            mPassword.setError("请填写至少6位数密码");
            isPass = false;
        }else {
            mPassword.setError(null);
        }

        return isPass;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_sign_in;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        mPhone.setText(mPhoneNum);
    }

    @Override
    public void onStart() {
        super.onStart();
        //读取mysp.xml中存放的用户名、密码，将其显示到用户名、密码的输入框之中
        Map<String,String> data=sp.read();
        mPhone.setText(data.get("username"));
        mPassword.setText(data.get("password"));

    }

}
