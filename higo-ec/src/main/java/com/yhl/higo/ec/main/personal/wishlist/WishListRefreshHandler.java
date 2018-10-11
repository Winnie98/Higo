package com.yhl.higo.ec.main.personal.wishlist;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yhl.higo.app.Higo;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.identify.IdentifyDelegate;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.ec.main.market.MarketAdapter;
import com.yhl.higo.ec.main.market.MarketDataConverter;
import com.yhl.higo.ec.main.personal.publish.publishRefreshHandler;
import com.yhl.higo.ec.sign.SignInCodeDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.IError;
import com.yhl.higo.net.callback.IFailure;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.recycler.DataConverter;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.ui.refresh.PaginBean;
import com.yhl.higo.util.log.HigoLogger;

import java.util.List;

import static com.blankj.utilcode.util.Utils.getContext;

/**
 * Created by Administrator on 2018/5/12/012.
 */

public class WishListRefreshHandler implements
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener
        {

    private final SwipeRefreshLayout REFRESH_LAYOUT;
    private final PaginBean BEAN;
    private final RecyclerView RECYCLERVIEW;
    private WishListAdapter mAdapter  =  null;
    private final DataConverter CONVERTER;
    private final WishListDelegate DELEGATE;


    private WishListRefreshHandler(SwipeRefreshLayout swipeRefreshLayout,
                                   RecyclerView recyclerView,
                                   DataConverter converter,
                                   PaginBean bean,
                                   WishListDelegate delegate) {
        this.REFRESH_LAYOUT = swipeRefreshLayout;
        this.RECYCLERVIEW = recyclerView;
        this.CONVERTER = converter;
        this.BEAN = bean;
        DELEGATE = delegate;
        REFRESH_LAYOUT.setOnRefreshListener(this);
    }

    public static WishListRefreshHandler create(SwipeRefreshLayout swipeRefreshLayout,
                                                RecyclerView recyclerView,
                                                DataConverter converter,
                                                WishListDelegate delegate){
        return new WishListRefreshHandler(swipeRefreshLayout,recyclerView,converter,new PaginBean(), delegate);
    }

    private void refresh(){
        REFRESH_LAYOUT.setRefreshing(true);
        Higo.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //进行一些网络请求
                myWishListFirstPage();
                REFRESH_LAYOUT.setRefreshing(false);
            }
        },2000);
    }

    public void myWishListFirstPage(){
        BEAN.setDelayed(1000);
        RestClient.builder()
                .url("user/wishlist/get_wishlist_list.do")
                .params("pageNum",1)
                .params("pageSize",10)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("WISH_LIST",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
//                                final StaggeredGridLayoutManager manager =
//                                        new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
//                                RECYCLERVIEW.setLayoutManager(manager);

                                final LinearLayoutManager manager = new LinearLayoutManager(getContext());
                                RECYCLERVIEW.setLayoutManager(manager);

                                final JSONObject object = JSON.parseObject(response).getJSONObject("data");
                                BEAN.setPageIndex(object.getInteger("pageNum"))
                                        .setPageSize(object.getInteger("pageSize"))
                                        .setIsHasNextPage(object.getBoolean("hasNextPage"));

                                final List<MultipleItemEntity> data =
                                        new WishListDataConverter().setJsonData(response).convert();

                                mAdapter = new WishListAdapter(data,DELEGATE);

                                mAdapter.setOnLoadMoreListener(WishListRefreshHandler.this,RECYCLERVIEW);

                                RECYCLERVIEW.setAdapter(mAdapter);



                                BEAN.addIndex();
                                BEAN.addPageSize();
                                break;
                            case 3:
//                    getSupportDelegate().start(new SignInDelegate());
                                DELEGATE.getSupportDelegate().start(new SignInCodeDelegate());
                                REFRESH_LAYOUT.setRefreshing(false);
                                break;
                            default:
                                REFRESH_LAYOUT.setRefreshing(false);
                                break;
                        }
                    }
                })
                .build()
                .get();
    }

    private void paging() {
        final int index = BEAN.getPageIndex();
        final int pageSize = BEAN.getPageSize();

        final boolean hasNextPage = BEAN.getIsHasNextPage();

        if (!hasNextPage) {
            mAdapter.loadMoreEnd(true);
        } else {
            Higo.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    RestClient.builder()
                            .url("user/wishlist/get_wishlist_list.do")
                            .params("pageNum",index)
//                            .params("pageSize",pageSize)
                            .success(new ISuccess() {
                                @Override
                                public void onSuccess(String response) {
                                    HigoLogger.d("WISH_LIST",response);
                                    HigoLogger.json("paging", response);

                                    final int status = JSON.parseObject(response).getInteger("status");
                                    switch (status){
                                        case 0:
//                                            final StaggeredGridLayoutManager manager =
//                                                    new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
//                                            RECYCLERVIEW.setLayoutManager(manager);
                                            final JSONObject object = JSON.parseObject(response).getJSONObject("data");
                                            BEAN.setPageIndex(object.getInteger("pageNum"))
                                                    .setPageSize(object.getInteger("pageSize"))
                                                    .setIsHasNextPage(object.getBoolean("hasNextPage"));
                                            //设置Adapter
                                            mAdapter.addData(CONVERTER.setJsonData(response).convert());
                                            mAdapter.loadMoreComplete();
//                                            mAdapter = MultipleRecyclerAdapter.create(CONVERTER.setJsonData(response));

//                                            mAdapter.setOnLoadMoreListener(publishRefreshHandler.this,RECYCLERVIEW);

//                                            RECYCLERVIEW.setAdapter(mAdapter);
                                            BEAN.addIndex();
                                            BEAN.addPageSize();
                                            break;

                                        case 1:
                                            final String msg = JSON.parseObject(response).getString("msg");
                                            Toast.makeText(Higo.getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                            break;
                                        default:

                                            break;
                                    }

                                }
                            })
                            .build()
                            .get();
                }
            }, 1000);
        }
    }


    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onLoadMoreRequested() {
        paging();
    }
}
