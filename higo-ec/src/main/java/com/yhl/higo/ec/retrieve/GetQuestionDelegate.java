package com.yhl.higo.ec.retrieve;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.ec.sign.SignHandler;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.util.CountDownTimerUtils;
import com.yhl.higo.util.log.HigoLogger;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/4/004.
 */

public class GetQuestionDelegate extends HigoDelegate {


    @BindView(R2.id.edt_get_question_phone)
    EditText mGetQuestionPhone = null;
    @BindView(R2.id.edt_get_question_code)
    EditText mCode = null;
    @BindView(R2.id.txt_get_question_code)
    TextView mGetCode = null;

    @OnClick(R2.id.txt_get_question_code)
    void onClickGetCode(){
        CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(mGetCode, 60000, 1000);
        mCountDownTimerUtils.start();

        RestClient.builder()
                .url("user/code/send_code.do")
                .params("phone",mGetQuestionPhone.getText().toString())
                .params("type", IdentifyItemType.IDENTIFY_RESET_PWD)
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

    @OnClick(R2.id.btn_get_question_confirm)
    void onClickGetQuestion(){
       if (checkForm()){
           RestClient.builder()
                   .url("user/code/check_code.do")
                   .params("phone",mGetQuestionPhone.getText().toString())
                   .params("code",mCode.getText().toString())
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
                                           .url("user/user/get_question.do")
                                           .params("phone",mGetQuestionPhone.getText().toString())
                                           .success(new ISuccess() {
                                               @Override
                                               public void onSuccess(String response) {
                                                   HigoLogger.d("GET_QUESTION",response);
                                                   final int status = JSON.parseObject(response).getInteger("status");
                                                   switch (status){
                                                       case 0:
                                                           final String data = JSON.parseObject(response).getString("data");

                                                           final CheckAnswerDelegate delegate  = CheckAnswerDelegate.create(mGetQuestionPhone.getText().toString(),data);
                                                           getSupportDelegate().start(delegate);

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
                                           .get();
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
        final String phone =mGetQuestionPhone.getText().toString();
        final String code =mCode.getText().toString();

        boolean isPass = true;//是否通过

        if (phone.isEmpty()||phone.length()!=11){
            mGetQuestionPhone.setError("手机号码错误");
            isPass = false;
        }else {
            mGetQuestionPhone.setError(null);
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
        return R.layout.delegate_get_question;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {

    }
}
