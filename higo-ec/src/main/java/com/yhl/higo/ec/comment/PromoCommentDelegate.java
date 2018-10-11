package com.yhl.higo.ec.comment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.sign.SignInCodeDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
//import com.yhl.higo.util.AndroidBug5497Workaround;
import com.yhl.higo.util.log.HigoLogger;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/8/008.
 */

public class PromoCommentDelegate
        extends HigoDelegate{

    private int mPromoId = -1;
//    private PaginBean BEAN;
    private CommentAdapter mAdapter  =  null;
//    private DataConverter CONVERTER;

    private CommentRefreshHandler mRefreshHandler = null;



    @BindView(R2.id.edt_promo_comment_add)
    EditText mComment;
    @BindView(R2.id.rl_promo_comment)
    RecyclerView mRecyclerView = null;
    @BindView(R2.id.swip_refresh_comment_promo)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R2.id.img_promo_comment_send)
    ImageView  mSendImage = null;


    //发布评论
    @OnClick(R2.id.img_promo_comment_send)
    void onClickSendComment(){
        if (mComment.getText().toString().isEmpty()){
            Toast.makeText(getContext(),"评论内容不能为空",Toast.LENGTH_SHORT).show();
        }else {
            RestClient.builder()
                    .url("user/comment/add_comment.do")
                    .params("promoId",mPromoId)
                    .params("content",mComment.getText().toString())
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            HigoLogger.d("SEND_COMMENT",response);
                            final int status  = JSON.parseObject(response).getInteger("status");
                            final String msg  = JSON.parseObject(response).getString("msg");
                            switch (status){
                                case 0:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    mComment.setText("");
                                    mRefreshHandler.firstCommentPage(1,mPromoId);
                                    break;
                                case 2:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    break;
                                case 3:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    getSupportDelegate().start(new SignInCodeDelegate());
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            mPromoId = args.getInt("promoId");
        }

        //android 软键盘在全屏下和沉浸式状态下EditText被覆盖解决
//        AndroidBug5497Workaround.assistActivity(getActivity());

    }

    public static PromoCommentDelegate create(int promoId) {
        final Bundle args = new Bundle();
        args.putInt("promoId", promoId);
        final PromoCommentDelegate delegate = new PromoCommentDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_comment_promo;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        mRefreshHandler =  CommentRefreshHandler.create(mSwipeRefreshLayout,mRecyclerView,new PromoCommentDataConverter(),PromoCommentDelegate.this);
        mRefreshHandler.firstCommentPage(1,mPromoId);
        initRecyclerView();
        initRefreshLayout();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    private void initRecyclerView(){
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);

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
    public void onSupportVisible() {
        super.onSupportVisible();
        mRefreshHandler.firstCommentPage(1,mPromoId);
    }
}
