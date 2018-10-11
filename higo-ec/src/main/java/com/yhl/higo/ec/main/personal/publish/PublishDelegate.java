package com.yhl.higo.ec.main.personal.publish;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;

//import com.yhl.higo.ui.refresh.RefreshHandler;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/6/7/007.
 */

public class PublishDelegate extends HigoDelegate {
    @BindView(R2.id.recycler_publish)
    RecyclerView mRecyclerView = null;
    @BindView(R2.id.swipe_refresh_publish)
    SwipeRefreshLayout mRefreshLayout = null;

    private final String URL = "user/promo/get_user_promo_list.do";

    private publishRefreshHandler mRefreshHandler = null;

    @Override
    public Object setLayout() {
        return R.layout.delegate_publish;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        initRecyclerView();
        initRefreshLayout();
        mRefreshHandler =  publishRefreshHandler.create(mRefreshLayout,mRecyclerView,new PublishDataConverter(),URL,PublishDelegate.this);
        mRefreshHandler.myPublishFirstPage();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    private void initRefreshLayout(){
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        mRefreshLayout.setProgressViewOffset(true,120,300);
    }

    private void initRecyclerView(){
//        final GridLayoutManager manager = new GridLayoutManager(getContext(),4);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);

//        mRecyclerView.addOnItemTouchListener(IndexItemClickListener.create(this));
    }
}
