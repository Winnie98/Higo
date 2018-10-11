package com.yhl.higo.ec.sign;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.util.Patterns;
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

/**
 * Created by Administrator on 2018/4/25/025.
 */

public class SignUpDelegate extends HigoDelegate {

    private ISignListener mISignListener = null;
    private String mPhoneNum;

    @BindView(R2.id.edit_sign_up_name)
    TextInputEditText mName = null;
    @BindView(R2.id.edit_sign_up_phone)
    TextInputEditText mPhone = null;
    @BindView(R2.id.edit_sign_up_email)
    TextInputEditText mEmail = null;
    @BindView(R2.id.edit_sign_up_question)
    TextInputEditText mQuestion  = null;
    @BindView(R2.id.edit_sign_up_answer)
    TextInputEditText mAnswer = null;
    @BindView(R2.id.edit_sign_up_password)
    TextInputEditText mPassword = null;
    @BindView(R2.id.edit_sign_up_re_password)
    TextInputEditText mRePassword = null;
    @BindView(R2.id.edit_sign_up_code)
    TextInputEditText mSignUpCode = null;
    @BindView(R2.id.txt_get_sign_up_code)
    TextView mGetCode = null;

    @OnClick(R2.id.txt_get_sign_up_code)
    void onClickGetCode(){
        CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(mGetCode, 60000, 1000);
        mCountDownTimerUtils.start();

        RestClient.builder()
                .url("user/code/send_code.do")
                .params("phone",mPhone.getText().toString())
                .params("type",IdentifyItemType.IDENTIFY_SIGN_UP)
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


    public static SignUpDelegate create(String phone){
        final Bundle args = new Bundle();
        args.putString("phone",phone);
        final SignUpDelegate delegate = new SignUpDelegate();
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
        }
    }


    @OnClick(R2.id.btn_sign_up)
    void onClickSignUp(){
        if (checkForm()) {
            RestClient.builder()
                    .url("user/code/check_code.do")
                    .params("phone",mPhone.getText().toString())
                    .params("code",mSignUpCode.getText().toString())
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            HigoLogger.d("CHECK_CODE",response);
                            final int status = JSON.parseObject(response).getInteger("status");
                            final String msg  = JSON.parseObject(response).getString("msg");
                            switch (status){
                                case 0:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    RestClient.builder()
                                            .url("user/user/register.do")
                                            .params("username",mName.getText().toString())
                                            .params("password", mPassword.getText().toString())
                                            .params("email",mEmail.getText().toString())
                                            .params("phone", mPhone.getText().toString())
                                            .params("question",mQuestion.getText().toString())
                                            .params("answer",mAnswer.getText().toString())
                                            .success(new ISuccess() {
                                                @Override
                                                public void onSuccess(String response) {
                                                    Log.w("onSuccess","success");
                                                    HigoLogger.json("USER_PROFILE", response);
                                                    final int status = JSON.parseObject(response).getInteger("status");
                                                    switch (status){
                                                        case 0:
                                                            SignHandler.onSignUp(response, mISignListener);
                                                            Toast.makeText(getContext(),"你已注册成功",Toast.LENGTH_LONG).show();
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

    }


    private boolean checkForm(){
        final String username = mName.getText().toString();
        final String phone =mPhone.getText().toString();
        final String email = mEmail.getText().toString();
        final String question = mQuestion.getText().toString();
        final String answer = mAnswer.getText().toString();
        final String password =mPassword.getText().toString();
        final String rePassword =mRePassword.getText().toString();
        final String code =mSignUpCode.getText().toString();

        boolean isPass = true;//是否通过

        if (phone.isEmpty()||phone.length()!=11){
            mPhone.setError("手机号码错误");
            isPass = false;
        }else {
            mPhone.setError(null);
        }

        if (code.isEmpty()||code.length()!=6){
            mSignUpCode.setError("请填写6位数验证码");
            isPass = false;
        }else {
            mSignUpCode.setError(null);
        }

        if (username.isEmpty()){
            mName.setError("请输入用户名");
        }else {
            mName.setError(null);
        }

        if (email.isEmpty()||!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmail.setError("错误的邮箱格式");
            isPass = false;
        }else {
            mEmail.setError(null);
        }

        if (phone.isEmpty()||phone.length()!=11){
            mPhone.setError("手机号码错误");
            isPass = false;
        }else {
            mPhone.setError(null);
        }

        if (question.isEmpty()){
            mQuestion.setError("请输入问题");
        }else {
            mQuestion.setError(null);
        }

        if (answer.isEmpty()){
            mAnswer.setError("请输入答案");
        }else {
            mAnswer.setError(null);
        }

        if (password.isEmpty()||password.length()<6){
            mPassword.setError("请填写至少6位数密码");
            isPass = false;
        }else {
            mPassword.setError(null);
        }

        if (rePassword.isEmpty()||rePassword.length()<6||!(rePassword.equals(password))){
            mRePassword.setError("密码验证错误");
            isPass = false;
        }else {
            mRePassword.setError(null);
        }

        return isPass;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_sign_up;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        mPhone.setText(mPhoneNum);
    }
}
