package com.yhl.higo.ec.identify;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.main.EcBottomDelegate;
import com.yhl.higo.ec.retrieve.CheckAnswerDelegate;
import com.yhl.higo.ec.sign.SignInDelegate;
import com.yhl.higo.ec.sign.SignUpDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.util.CountDownTimerUtils;
import com.yhl.higo.util.log.HigoLogger;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/25/025.
 */

public class IdentifyDelegate extends HigoDelegate {

    private int mType = -1;//1. 手机登录、2. 手机注册、3. 密码重置、4. 修改手机号码
    private String mPhoneNum  =  null;

    @BindView(R2.id.edit_phone)
    TextInputEditText mPhone = null;
    @BindView(R2.id.edit_identify)
    TextInputEditText mIdentity = null;
    @BindView(R2.id.txt_get_identify)
    TextView mGetTxt = null;

    @OnClick(R2.id.txt_get_identify)
    void onClickGetIdentity(){
        if (mPhone.getText().toString().equals("")){
            Toast.makeText(getContext(),"请输入手机号",Toast.LENGTH_SHORT).show();
        }else {
            CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(mGetTxt, 60000, 1000);
            mCountDownTimerUtils.start();

            RestClient.builder()
                    .url("user/code/send_code.do")
                    .params("phone",mPhone.getText().toString())
                    .params("type",mType)
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

    }
    @OnClick(R2.id.btn_next)
    void onClickIdentity(){
        RestClient.builder()
                .url("user/code/check_code.do")
                .params("phone",mPhone.getText().toString())
                .params("code",mIdentity.getText().toString())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("CHECK_CODE",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        final String msg  = JSON.parseObject(response).getString("msg");
                        switch (status){
                            case 0:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                switch (mType){
                                    case 1:
                                        //手机登录
//                                        getSupportDelegate().onDestroyView();
//                                        getSupportDelegate().onDestroy();
//                                        getSupportDelegate().pop();

                                        getSupportDelegate().start(SignInDelegate.create(mPhone.getText().toString()));

//                                        getSupportDelegate().pop();
//                                        getSupportDelegate().replaceFragment(SignInDelegate.create(mPhone.getText().toString()),false);
                                        break;
                                    case 2:
                                        //手机注册

//                                        getSupportDelegate().onDestroyView();
//                                        getSupportDelegate().onDestroy();

                                        getSupportDelegate().start(SignUpDelegate.create(mPhone.getText().toString()));
//                                        getSupportDelegate().replaceFragment(SignUpDelegate.create(mPhone.getText().toString()),false);

//                                        getSupportDelegate().pop();
                                        break;
                                    case 3:
                                        //密码重置
                                        RestClient.builder()
                                                .url("user/user/get_question.do")
                                                .params("phone",mPhone.getText().toString())
                                                .success(new ISuccess() {
                                                    @Override
                                                    public void onSuccess(String response) {
                                                        HigoLogger.d("GET_QUESTION",response);
                                                        final int status = JSON.parseObject(response).getInteger("status");
                                                        switch (status){
                                                            case 0:
                                                                final String data = JSON.parseObject(response).getString("data");

//                                                                getSupportDelegate().onDestroyView();
//                                                                getSupportDelegate().onDestroy();

//                                                                getSupportDelegate().pop();
//                                                                getSupportDelegate().onHiddenChanged(true);

                                                                final CheckAnswerDelegate delegate  = CheckAnswerDelegate.create(mPhone.getText().toString(),data);
                                                                getSupportDelegate().start(delegate);

//                                                                getSupportDelegate().startChild(delegate);

//                                                                getActivity().onBackPressed();
//                                                                getSupportDelegate().replaceFragment(delegate,false);

//                                                                getSupportDelegate().pop();

//                                                                getSupportDelegate().onDestroyView();
//                                                                getSupportDelegate().onDestroy();

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
                                    case 4:
                                        //修改手机号码
                                        Bundle bundle = new Bundle();
                                        bundle.putString("phone", mPhone.getText().toString());
                                        getSupportDelegate().setFragmentResult(RESULT_OK, bundle);

                                        getSupportDelegate().onDestroyView();
                                        getSupportDelegate().onDestroy();
                                        getSupportDelegate().pop();
                                        break;
                                    default:
                                        break;
                                }
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

    public static IdentifyDelegate create(String phone, int type) {
        final Bundle args = new Bundle();
        args.putString("phone",phone);
        args.putInt("type",type);
        final IdentifyDelegate delegate = new IdentifyDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    public static IdentifyDelegate create(int type) {
        final Bundle args = new Bundle();
        args.putInt("type",type);
        final IdentifyDelegate delegate = new IdentifyDelegate();
        delegate.setArguments(args);
        return delegate;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            mType = args.getInt("type");

            if (mType == IdentifyItemType.IDENTIFY_MODIFY_PHONE){
                mPhoneNum = args.getString("phone");
            }

        }
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_identify;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        mPhone.setText(mPhoneNum);
    }
}
