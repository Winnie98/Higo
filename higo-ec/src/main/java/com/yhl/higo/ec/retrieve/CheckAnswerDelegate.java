package com.yhl.higo.ec.retrieve;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.main.EcBottomDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.util.log.HigoLogger;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/4/004.
 */

public class CheckAnswerDelegate extends HigoDelegate {

    private String mPhone = null;
    private String mQuestion = null;

    @BindView(R2.id.tv_check_answer_phone)
    AppCompatTextView mCheckPhone = null;
    @BindView(R2.id.tv_check_answer_question)
    AppCompatTextView mCheckQuestion = null;

    @BindView(R2.id.edt_check_answer_answer)
    AppCompatEditText mCheckAnswer = null;

    @OnClick(R2.id.image_close_check)
    void onClickClose(){
        getSupportDelegate().startWithPop(new EcBottomDelegate());
    }

    @OnClick(R2.id.btn_check_answer_confirm)
    void onClickCheckAnswer(){
        RestClient.builder()
                .url("user/user/check_answer.do")
                .params("phone",mPhone)
                .params("question",mQuestion)
                .params("answer",mCheckAnswer.getText().toString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("CHECK_ANSWER",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final String data = JSON.parseObject(response).getString("data");
                                HigoLogger.d("CHECK_ANSWER",data);

                                final ResetPwdDelegate delegate  = ResetPwdDelegate.create(mPhone,data);
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
                .post();
    }


    @Override
    public Object setLayout() {
        return R.layout.delegate_check_answer;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        final Bundle args = getArguments();
        if (args != null) {
            mPhone = args.getString("phone");
            mQuestion = args.getString("question");
            mCheckPhone.setText(mPhone);
            mCheckQuestion.setText(mQuestion);
        }

    }

    public static CheckAnswerDelegate create(String phone, String question) {
        final Bundle args = new Bundle();
        args.putString("phone",phone);
        args.putString("question",question);
        final CheckAnswerDelegate delegate = new CheckAnswerDelegate();
        delegate.setArguments(args);
        return delegate;
    }
}
