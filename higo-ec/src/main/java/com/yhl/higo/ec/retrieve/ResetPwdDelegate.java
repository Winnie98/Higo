package com.yhl.higo.ec.retrieve;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.main.EcBottomDelegate;
import com.yhl.higo.ec.sign.SignInDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.util.log.HigoLogger;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/4/004.
 */

public class ResetPwdDelegate extends HigoDelegate {

    private String mPhone = null;
    private String mToken = null;

    @BindView(R2.id.tv_reset_pwd_phone)
    AppCompatTextView mResetPhone = null;
    @BindView(R2.id.edt_reset_pwd_password)
    AppCompatEditText mResetPwd = null;
    @BindView(R2.id.edt_reset_pwd_re_password)
    AppCompatEditText mResetRePwd = null;

    @OnClick(R2.id.image_close_reset)
    void onClickClose(){
        getSupportDelegate().startWithPop(new EcBottomDelegate());
    }

    @OnClick(R2.id.btn_reset_pwd_confirm)
    void onClickResetPwd(){
        if (checkForm()){
            RestClient.builder()
                    .url("user/user/reset_password.do")
                    .params("phone",mPhone)
                    .params("password",mResetPwd.getText().toString())
                    .params("forgetToken",mToken)
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            HigoLogger.d("RESET_PWD",response);
                            final int status = JSON.parseObject(response).getInteger("status");
                            final String msg = JSON.parseObject(response).getString("msg");
                            switch (status) {
                                case 0:
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                    getSupportDelegate().pop();
                                    getSupportDelegate().pop();
                                    getSupportDelegate().pop();

                                    break;
                                case 1:
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
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

    @Override
    public Object setLayout() {
        return R.layout.delegate_reset_password;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        final Bundle args = getArguments();
        if (args != null) {
            mPhone = args.getString("phone");
            mToken = args.getString("token");
            mResetPhone.setText(mPhone);
        }
    }

    public static ResetPwdDelegate create(String phone,String token) {
        HigoLogger.d("TOKEN",token);
        final Bundle args = new Bundle();
        args.putString("phone",phone);
        args.putString("token",token);
        final ResetPwdDelegate delegate = new ResetPwdDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    private boolean checkForm(){
        final String password =mResetPwd.getText().toString();
        final String rePassword =mResetRePwd.getText().toString();

        boolean isPass = true;//是否通过


        if (password.isEmpty()||password.length()<6){
            mResetPwd.setError("请填写至少6位数密码");
            isPass = false;
        }else {
            mResetPwd.setError(null);
        }

        if (rePassword.isEmpty()||rePassword.length()<6||!(rePassword.equals(password))){
            mResetRePwd.setError("密码验证错误");
            isPass = false;
        }else {
            mResetRePwd.setError(null);
        }

        return isPass;
    }


}
