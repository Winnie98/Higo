package com.yhl.higo.ec.main.personal.profile;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.ec.identify.IdentifyDelegate;
import com.yhl.higo.ec.main.personal.modifypwd.ModifyPwdDelegate;
import com.yhl.higo.ec.sign.SignInDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.image.GlideApp;
import com.yhl.higo.util.callback.CallBackManager;
import com.yhl.higo.util.callback.CallbackType;
import com.yhl.higo.util.callback.IGlobalCallback;
import com.yhl.higo.util.log.HigoLogger;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2018/5/25/025.
 */

public class UserInfoDelegate extends HigoDelegate{
    private String avatarUri = null;

    @BindView(R2.id.edt_user_info_username)
    EditText mUsername = null;
    @BindView(R2.id.edt_user_info_email)
    EditText mEdtEmail = null;
    @BindView(R2.id.txt_user_info_phone)
    TextView mTxtPhone = null;
    @BindView(R2.id.edt_user_info_question)
    EditText mEdtQuestion = null;
    @BindView(R2.id.edt_user_info_answer)
    EditText mEdtAnswer = null;
    @BindView(R2.id.txt_user_info_sell_count)
    TextView mSellCount = null;
    @BindView(R2.id.edt_user_info_avatar)
    CircleImageView mAvatar = null;
    @BindView(R2.id.user_info_rb_bar)
    RatingBar mRtBar = null;

    @OnClick(R2.id.edt_user_info_avatar)
    void onClickEditAvatar(){
        //开始照相机或选择图片
        CallBackManager.getInstance()
                .addCallback(CallbackType.ON_CROP, new IGlobalCallback<Uri>() {
                    @Override
                    public void executeCallback(Uri args) {
                        HigoLogger.d("ON_CROP", args);
                        Glide.with(getContext())
                                .load(args)
                                .into(mAvatar);

                        HigoLogger.d("FILE",args.getPath());

                        RestClient.builder()
                                .url("common/file/upload.do")
//                                .file(args.getPath())
                                .file(new File(args.getPath()))
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        HigoLogger.d("UPLOAD_AVATAR",response);
                                        final int status = JSON.parseObject(response).getInteger("status");
                                        final String msg = JSON.parseObject(response).getString("msg");
                                        switch (status){
                                            case 0:
                                                Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                                                final String uri = JSON.parseObject(response).getJSONObject("data").getString("uri");
                                                final String url = JSON.parseObject(response).getJSONObject("data").getString("url");

                                                avatarUri = JSON.parseObject(response).getJSONObject("data").getString("uri");

                                                break;
                                            case 1:
                                                Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                                                getParentDelegate().getSupportDelegate().start( new SignInDelegate());
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                })
                                .build()
                                .upload();

                    }
                });
        startCameraWithCheck();
    }

    @OnClick(R2.id.rl_edt_phone)
    void onClickEdtPhone(){
        getSupportDelegate().startForResult(IdentifyDelegate.create(mTxtPhone.getText().toString(), IdentifyItemType.IDENTIFY_MODIFY_PHONE),1);
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK ) {

            final String phone = data.getString("phone");
            mTxtPhone.setText(phone);
        }
    }

    @OnClick(R2.id.icon_user_info_back)
    void onClickBack(){
        getSupportDelegate().pop();

    }
    //修改密码
    @OnClick(R2.id.modify_pwd_layout)
    void modifyPwd(){
        getSupportDelegate().start(new ModifyPwdDelegate());
    }

    @OnClick(R2.id.txt_confirm)
    void onClickUpdateUserInfo(){
        if (avatarUri == null){
            RestClient.builder()
                    .url("user/user/update_user_info.do")
                    .params("username",mUsername.getText().toString())
                    .params("email",mEdtEmail.getText().toString())
                    .params("phone",mTxtPhone.getText().toString())
                    .params("question",mEdtQuestion.getText().toString())
                    .params("answer", mEdtAnswer.getText().toString())
//                .params("id_images","")
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            HigoLogger.d("UPDATE_INFO",response);
                            final int status = JSON.parseObject(response).getInteger("status");
                            final String msg = JSON.parseObject(response).getString("msg");
                            switch (status){
                                case 0:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                                    getSupportDelegate().pop();
                                    break;
                                case 1:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
//                                getSupportDelegate().start( new SignInDelegate());
                                    break;
                                default:
                                    break;
                            }
                        }
                    })
                    .build()
                    .post();
        }else {
            RestClient.builder()
                    .url("user/user/update_user_info.do")
                    .params("avatar",avatarUri)
                    .params("username",mUsername.getText().toString())
                    .params("email",mEdtEmail.getText().toString())
                    .params("phone",mTxtPhone.getText().toString())
                    .params("question",mEdtQuestion.getText().toString())
                    .params("answer", mEdtAnswer.getText().toString())
//                .params("id_images","")
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            HigoLogger.d("UPDATE_INFO",response);
                            final int status = JSON.parseObject(response).getInteger("status");
                            final String msg = JSON.parseObject(response).getString("msg");
                            switch (status){
                                case 0:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                                    getSupportDelegate().pop();
                                    break;
                                case 1:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
//                                getSupportDelegate().start( new SignInDelegate());
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

    public void getUserInfo(){
        RestClient.builder()
                .url("user/user/get_current_user_info.do")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("USER_INFO",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final String username = JSON.parseObject(response).getJSONObject("data").getString("username");
                                final String email = JSON.parseObject(response).getJSONObject("data").getString("email");
                                final String phone = JSON.parseObject(response).getJSONObject("data").getString("phone");
                                final double totalScore = JSON.parseObject(response).getJSONObject("data").getDouble("totalScore");
                                final double score = JSON.parseObject(response).getJSONObject("data").getDouble("score");
                                final int sellCount = JSON.parseObject(response).getJSONObject("data").getInteger("sellCount");
//                                final String createTime = JSON.parseObject(response).getJSONObject("data").getString("createTime");
                                final String avatar = JSON.parseObject(response).getJSONObject("data").getString("avatar");
                                final String question = JSON.parseObject(response).getJSONObject("data").getString("question");
                                final String answer = JSON.parseObject(response).getJSONObject("data").getString("answer");

                                mUsername.setText(username);
                                mEdtEmail.setText(email);
                                mTxtPhone.setText(phone);
                                mRtBar.setRating((float) score);
                                mSellCount.setText(String.valueOf(sellCount));
//                                mCreateTime.setText(createTime);
                                GlideApp.with(getContext())
                                        .load("http://img.higo.party/"+avatar)
                                        .into(mAvatar);
                                mEdtQuestion.setText(question);
                                mEdtAnswer.setText(answer);

                                break;
                            case 1:
                                Toast.makeText(getContext(),"你还未登录，请登录后再使用",Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .build()
                .get();
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_user_profile;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        getUserInfo();
        mRtBar.setEnabled(false);
    }
}
