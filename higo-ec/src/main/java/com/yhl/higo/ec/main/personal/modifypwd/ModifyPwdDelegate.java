package com.yhl.higo.ec.main.personal.modifypwd;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.sign.SignInDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.util.log.HigoLogger;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/31/031.
 */

public class ModifyPwdDelegate extends HigoDelegate {


    @BindView(R2.id.edt_modify_pwd_old)
    EditText mOldPwd = null;
    @BindView(R2.id.edt_modify_pwd_new)
    EditText mNewPwd = null;
    @BindView(R2.id.edt_modify_pwd_ensure)
    EditText mEnsurePwd = null;

    @OnClick(R2.id.btn_modify_pwd_confirm)
    void onClickModifyPwd(){
        if (checkForm()){
            RestClient.builder()
//                    .url("user/reset_password.do")
                    .url("user/user/update_password.do")
                    .params("oldPassword",mOldPwd.getText().toString())
                    .params("newPassword",mNewPwd.getText().toString())
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            HigoLogger.d("MODIFY_PASSWORD",response);
                            final int status = JSON.parseObject(response).getInteger("status");
                            final String msg = JSON.parseObject(response).getString("msg");
                            switch (status){
                                case 0:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    mOldPwd.setText("");
                                    mNewPwd.setText("");
                                    mEnsurePwd.setText("");
                                    Toast.makeText(getContext(),"您的身份已过期，请重新登录！",Toast.LENGTH_SHORT).show();
                                    getSupportDelegate().start(new SignInDelegate());
                                    break;
                                case 1:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    if (msg.equals("用户未登录")){
                                        getSupportDelegate().start(new SignInDelegate());
                                    }
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
        return R.layout.delegate_modify_pwd;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {

    }

    private boolean checkForm(){
        final String oldPwd = mOldPwd.getText().toString();
        final String newPwd =mNewPwd.getText().toString();
        final String ensurePwd =mEnsurePwd.getText().toString();

        boolean isPass = true;//是否通过

        if (oldPwd.isEmpty()){
            mOldPwd.setError("请输入旧密码");
        }else {
            mOldPwd.setError(null);
        }

        if (newPwd.isEmpty()||newPwd.length()<6){
            mNewPwd.setError("请填写至少6位数密码");
            isPass = false;
        }else if (newPwd.equals(oldPwd)){
            mNewPwd.setError("新的密码不能与旧密码相同");
        }else {
            mNewPwd.setError(null);
        }

        if (ensurePwd.isEmpty()||ensurePwd.length()<6||!(ensurePwd.equals(newPwd))){
            mEnsurePwd.setError("密码验证错误");
            isPass = false;
        }else {
            mEnsurePwd.setError(null);
        }

        return isPass;
    }


}
