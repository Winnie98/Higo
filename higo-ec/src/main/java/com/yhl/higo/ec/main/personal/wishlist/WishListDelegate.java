package com.yhl.higo.ec.main.personal.wishlist;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.identify.IdentifyDelegate;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.ec.main.market.MarketDataConverter;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.util.log.HigoLogger;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/31/031.
 */

public class WishListDelegate extends HigoDelegate
//        implements ISuccess
{

    private boolean mIsManager = false;//标记是否处于管理状态
    private boolean mIsSelectAll = false;//标记是否全选
    //心愿单数量标记
    private int mCurrentCount = 0;
    private int mTotalCount =  0;

    private WishListRefreshHandler mRefreshHandler = null;

    AlertDialog mDialog;
//    private Double mPrice;
//    private IconTextView mIconView = null;
//    private TextView mProductPrice;
    private WishListAdapter mAdapter = null;

    @BindView(R2.id.swip_refresh_wish_list)
    SwipeRefreshLayout mSwipeRefreshLayout = null;
    @BindView(R2.id.rv_wish_list)
    RecyclerView mRecyclerView = null;

    @OnClick(R2.id.icon_wish_list_back)
    void onClickWishListBack(){
        getSupportDelegate().pop();
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_wishlist;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {

        initRefreshLayout();

        mRefreshHandler =  WishListRefreshHandler.create(mSwipeRefreshLayout,mRecyclerView,new WishListDataConverter(),WishListDelegate.this);
        mRefreshHandler.myWishListFirstPage();
    }

    private void initRecyclerView(){
        final StaggeredGridLayoutManager manager =
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);

    }

    private void initRefreshLayout(){
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        mSwipeRefreshLayout.setProgressViewOffset(true,120,300);

        mRecyclerView.addOnItemTouchListener(WishListClickListener.create(this));


    }


}
