package com.yhl.higo.ec.main.personal.profile;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.sign.SignInDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.util.callback.CallBackManager;
import com.yhl.higo.util.callback.CallbackType;
import com.yhl.higo.util.callback.IGlobalCallback;
import com.yhl.higo.util.log.HigoLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/9/2/002.
 */

public class UserIdentityDelegate extends HigoDelegate {
    private final int POSITIVE = 1;
    private final int REVERSE = 2;
    private List<String> imgArray = new ArrayList<>();

    @BindView(R2.id.img_identity_positive)
    ImageView mPositivePhoto;
    @BindView(R2.id.img_identity_reverse)
    ImageView mReversePhoto;

    @OnClick(R2.id.icon_user_identity_back)
    void onClickIdentityBack(){
        getSupportDelegate().pop();
    }

    //选择照片
    public void pickPhoto(final int position){
        //开始照相机或选择图片
        CallBackManager.getInstance()
                .addCallback(CallbackType.ON_CROP, new IGlobalCallback<Uri>() {
                    @Override
                    public void executeCallback(Uri args) {
                        HigoLogger.d("ON_CROP", args);
                        if (position==POSITIVE){
                            Glide.with(getContext())
                                    .load(args)
                                    .into(mPositivePhoto);
                        }else if (position==REVERSE){
                            Glide.with(getContext())
                                    .load(args)
                                    .into(mReversePhoto);
                        }

                        RestClient.builder()
                                .url("common/file/upload.do")
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

                                                imgArray.add(uri);
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

    @OnClick(R2.id.img_identity_positive)
    void onClickPickPositive(){
       pickPhoto(POSITIVE);
    }

    @OnClick(R2.id.img_identity_reverse)
    void onClickPickReverse(){
        pickPhoto(REVERSE);
    }
    //身份认证
    @OnClick(R2.id.btn_user_identity_submit)
    void onClickIdentitySubmit(){
        final String idImages = listToString(imgArray);
        HigoLogger.d("mIdImages_User",idImages);

        RestClient.builder()
                .url("user/user/update_user_info.do")
                .params("idImages",idImages)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("IDENTITY",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        final String msg = JSON.parseObject(response).getString("msg");
                        switch (status) {
                            case 0:
                                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                getSupportDelegate().pop();
                                break;
                            case 1:
                                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                getParentDelegate().getSupportDelegate().start(new SignInDelegate());
                                break;
                            default:
                                break;
                        }
                    }
                })
                .build()
                .post();

    }

    //把list转换为一个用逗号分隔的字符串
    public static String listToString(List list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    sb.append(list.get(i) + ",");
                } else {
                    sb.append(list.get(i));
                }
            }
        }
        return sb.toString();
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_user_identity;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {

    }
}
