package com.yhl.higo.ec.main.personal.feeedback;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.joanzapata.iconify.widget.IconTextView;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;

import butterknife.BindView;
import butterknife.OnClick;

//import com.yhl.higo.ui.refresh.RefreshHandler;

/**
 * Created by Administrator on 2018/6/7/007.
 */

public class FeedbackDelegate extends HigoDelegate {
    @BindView(R2.id.recycler_feedback)
    RecyclerView mRecyclerView = null;
    @BindView(R2.id.swipe_refresh_feedback)
    SwipeRefreshLayout mRefreshLayout = null;
    @BindView(R2.id.icon_feedback)
    IconTextView mIconBack = null;

    @OnClick(R2.id.icon_feedback)
    void onClickBack(){
        getSupportDelegate().pop();
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_feedback;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        initRecyclerView();
        initRefreshLayout();
        String URL = "user/feedback/get_feedback_list.do";
        FeedbackRefreshHandler mRefreshHandler = FeedbackRefreshHandler.create(mRefreshLayout, mRecyclerView, new FeedbackDataConverter(), URL, this);
        mRefreshHandler.myFeedbackFirstPage();

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
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);

    }
}
