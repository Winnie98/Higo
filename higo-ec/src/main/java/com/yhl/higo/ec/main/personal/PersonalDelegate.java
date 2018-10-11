package com.yhl.higo.ec.main.personal;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.yhl.higo.delegates.bottom.BottomItemDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.main.EcBottomDelegate;
import com.yhl.higo.ec.main.personal.address.AddressDelegate;
import com.yhl.higo.ec.main.personal.profile.UseExamineDelegate;
import com.yhl.higo.ec.main.personal.profile.UseExaminePassDelegate;
import com.yhl.higo.ec.main.personal.profile.UserIdentityDelegate;
import com.yhl.higo.ec.main.personal.publish.PublishDelegate;
import com.yhl.higo.ec.main.personal.feeedback.FeedbackDelegate;
import com.yhl.higo.ec.main.personal.wishlist.WishListDelegate;
import com.yhl.higo.ec.main.personal.order.OrderListDelegate;
import com.yhl.higo.ec.main.personal.profile.UserInfoDelegate;
import com.yhl.higo.ec.save.ShareHelper;
import com.yhl.higo.ec.sign.SignHandler;
import com.yhl.higo.ec.sign.SignInCodeDelegate;
import com.yhl.higo.ec.sign.SignInDelegate;
import com.yhl.higo.ec.sign.SignUpDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.IError;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.image.GlideApp;
import com.yhl.higo.util.callback.CallBackManager;
import com.yhl.higo.util.callback.CallbackType;
import com.yhl.higo.util.callback.IGlobalCallback;
import com.yhl.higo.util.log.HigoLogger;

import java.io.File;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2018/5/20/020.
 */

public class PersonalDelegate extends BottomItemDelegate {
    public static final String ORDER_STATUS = "ORDER_STATUS";

    private Bundle mArgs = null;
    private double mBalance;


    @BindView(R2.id.tv_user_name)
    TextView mUsername = null;
    @BindView(R2.id.img_user_avatar)
    CircleImageView mAvatar  = null;
    @BindView(R2.id.txt_identity)
    TextView mIsIdentity = null;

    @BindView(R2.id.swipe_refresh_personal)
    SwipeRefreshLayout mSwipeRefreshLayout = null;

    @BindView(R2.id.ll_person_layout)
    LinearLayout mPersonLayout = null;
    @BindView(R2.id.ll_login_layout)
    LinearLayout mLoginLayout = null;
    @BindView(R2.id.ll_user_layout)
    LinearLayout mUserLayout = null;
    @BindView(R2.id.user_info_balance)
    TextView mTxtBalance = null;


    //访问服务器判断用户是否登ll_user_layout录
    public void initUserInfo(){
        RestClient.builder()
                .url("user/user/get_current_user_info.do")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("INFO",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                mPersonLayout.removeView(mUserLayout);
                                mPersonLayout.removeView(mLoginLayout);
//                                mPersonLayout.removeView(mLoginLayout);
                                mPersonLayout.addView(mUserLayout);
                                final String username = JSON.parseObject(response).getJSONObject("data").getString("username");
                                final String avatar = JSON.parseObject(response).getJSONObject("data").getString("avatar");
                                final Double balance = JSON.parseObject(response).getJSONObject("data").getDouble("balance");
                                mBalance = JSON.parseObject(response).getJSONObject("data").getDouble("balance");
                                final int userStatus  = JSON.parseObject(response).getJSONObject("data").getInteger("status");

                                if (userStatus==0){
                                    mIsIdentity.setText("立即认证");
                                }else if(userStatus==1){
                                    mIsIdentity.setText("管理员审核中");
                                }else if (userStatus==2){
                                    mIsIdentity.setText("认证成功");
                                }else if (userStatus==3){
                                    mIsIdentity.setText("认证失败");
                                }

                                mArgs.putDouble("balance",balance);

                                mUsername.setText(username);

                                GlideApp.with(getContext())
                                        .load("http://img.higo.party/"+avatar)
                                        .into(mAvatar);
                                mTxtBalance.setText(String.valueOf(mBalance));


                                mSwipeRefreshLayout.setRefreshing(false);

                                break;
                            case 3:
                                mPersonLayout.removeView(mUserLayout);
                                mPersonLayout.removeView(mLoginLayout);
                                mPersonLayout.addView(mLoginLayout);
                                mSwipeRefreshLayout.setRefreshing(false);
                                break;
                            default:
                                mSwipeRefreshLayout.setRefreshing(false);
                                break;
                        }
                    }
                })
                .build()
                .get();
    }

    @OnClick(R2.id.txt_sign_in)
    void onClickSignIn(){
        getParentDelegate().getSupportDelegate().start(new SignInCodeDelegate());
    }

    @OnClick(R2.id.txt_sign_up)
    void onClickSignUp(){
        getParentDelegate().getSupportDelegate().start(new SignUpDelegate());
    }

    @OnClick(R2.id.ll_all)
    void onClickAllOrder(){
        final OrderListDelegate delegate  = OrderListDelegate.create(0);
        getParentDelegate().getSupportDelegate().start(delegate);
    }
    @OnClick(R2.id.ll_pay)
    void onClickPayOrder(){
        final OrderListDelegate delegate  = OrderListDelegate.create(1);
        getParentDelegate().getSupportDelegate().start(delegate);
    }

    @OnClick(R2.id.ll_send)
    void onClickSendOrder(){
        final OrderListDelegate delegate  = OrderListDelegate.create(2);
        getParentDelegate().getSupportDelegate().start(delegate);
    }

    @OnClick(R2.id.ll_receive)
    void onClickReceiveOrder(){
        final OrderListDelegate delegate  = OrderListDelegate.create(3);
        getParentDelegate().getSupportDelegate().start(delegate);
    }

    @OnClick(R2.id.ll_complete)
    void onClickCompleteOrder(){
        final OrderListDelegate delegate  = OrderListDelegate.create(4);
        getParentDelegate().getSupportDelegate().start(delegate);
    }

    //设置头像
    @OnLongClick(R2.id.img_user_avatar)
    boolean onClickAvatar(){
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

                                                RestClient.builder()
                                                        .url("user/user/update_user_info.do")
                                                        .params("avatar",uri)
                                                        .success(new ISuccess() {
                                                            @Override
                                                            public void onSuccess(String response) {
                                                                HigoLogger.d("UPDATE_AVATAR",response);
                                                                final int status = JSON.parseObject(response).getInteger("status");
                                                                final String msg = JSON.parseObject(response).getString("msg");
                                                                switch (status) {
                                                                    case 0:
                                                                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
//                                                                        getSupportDelegate().pop();
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
        return true;
    }

    //查看用户信息
    @OnClick(R2.id.img_user_avatar)
    void onClickUserInfo(){
        getParentDelegate().getSupportDelegate().start(new UserInfoDelegate());
    }
    //用户身份认证
    @OnClick(R2.id.ll_user_identity)
    void onClickIdentity(){
        final String isIdentity = mIsIdentity.getText().toString();
        if(isIdentity.equals("立即认证")){

            getParentDelegate().getSupportDelegate().start(new UserIdentityDelegate());
        }else if (isIdentity.equals("管理员审核中")){

            getParentDelegate().getSupportDelegate().start(new UseExamineDelegate());
        }else if (isIdentity.equals("认证成功")){
            getParentDelegate().getSupportDelegate().start(new UseExaminePassDelegate());
        }else if (isIdentity.equals("认证失败")){
            getParentDelegate().getSupportDelegate().start(new UserIdentityDelegate());
        }
    }
    //收货地址
    @OnClick(R2.id.layout_address)
    void onClickAddress(){
        getParentDelegate().getSupportDelegate().start(new AddressDelegate());
    }
    //我的钱包
    @OnClick(R2.id.layout_wallet)
    void onClickWallet(){

    }
    //我的发布
    @OnClick(R2.id.layout_publish)
    void onClickPublish(){
        getParentDelegate().getSupportDelegate().start(new PublishDelegate());
    }
    //心愿单
    @OnClick(R2.id.layout_wishList)
    void onClickWishList(){
        getParentDelegate().getSupportDelegate().start(new WishListDelegate());
    }

    //反馈管理
    @OnClick(R2.id.layout_feedback)
    void onClickFeedBack(){
        getParentDelegate().getSupportDelegate().start(new FeedbackDelegate());
    }
    //确认退出
    @OnClick(R2.id.btn_exit)
    void onClickExit(){
        RestClient.builder()
                .url("user/user/logout.do")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("EXIT",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        final String msg = JSON.parseObject(response).getString("msg");
                        switch (status){
                            case 0:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                                final EcBottomDelegate ecBottomDelegate = getParentDelegate();

                                ecBottomDelegate.getSupportDelegate().replaceFragment(new EcBottomDelegate(),false);
                                break;
                            case 1:
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
        return R.layout.delegate_personal;
    }

    private void initRefreshLayout(){
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        mSwipeRefreshLayout.setProgressViewOffset(true,120,300);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArgs = new Bundle();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {

//        mPersonLayout.removeView(mUserLayout);
//        mPersonLayout.removeView(mLoginLayout);
        initUserInfo();
        initRefreshLayout();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                mPersonLayout.removeView(mUserLayout);
//                mPersonLayout.removeView(mLoginLayout);
                initUserInfo();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        initUserInfo();
    }

}
