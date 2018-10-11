package com.yhl.higo.ec.main.personal.publish;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yhl.higo.app.Higo;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.main.index.IndexDataConverter;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.recycler.DataConverter;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.ui.recycler.MultipleRecyclerAdapter;
import com.yhl.higo.ui.refresh.PaginBean;
import com.yhl.higo.util.log.HigoLogger;

import java.util.List;

/**
 * Created by Administrator on 2018/5/12/012.
 */

public class publishRefreshHandler implements
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener
        {

    private final SwipeRefreshLayout REFRESH_LAYOUT;
    private final PaginBean BEAN;
    private final RecyclerView RECYCLERVIEW;
    private PublishAdapter mAdapter  =  null;
    private final DataConverter CONVERTER;
    private final String URL;
    private final HigoDelegate DELEGATE;

    private publishRefreshHandler(SwipeRefreshLayout swipeRefreshLayout,
                                  RecyclerView recyclerView,
                                  DataConverter converter,
                                  PaginBean bean, String url,
                                  HigoDelegate delegate) {
        this.REFRESH_LAYOUT = swipeRefreshLayout;
        this.RECYCLERVIEW = recyclerView;
        this.CONVERTER = converter;
        this.BEAN = bean;
        URL = url;
        DELEGATE = delegate;
        REFRESH_LAYOUT.setOnRefreshListener(this);
    }

    public static publishRefreshHandler create(SwipeRefreshLayout swipeRefreshLayout,
                                               RecyclerView recyclerView,
                                               DataConverter converter,
                                               String url,
                                               HigoDelegate delegate){
        return new publishRefreshHandler(swipeRefreshLayout,recyclerView,converter,new PaginBean(), url, delegate);
    }

    private void refresh(){
        REFRESH_LAYOUT.setRefreshing(true);
        Higo.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //进行一些网络请求
                myPublishFirstPage();
                REFRESH_LAYOUT.setRefreshing(false);
            }
        },2000);
    }

    public void myPublishFirstPage(){
        BEAN.setDelayed(1000);
        RestClient.builder()
                .url(URL)
                .params("pageNum",1)
                .params("pageSize",10)
                .params("status","")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("MY_PUBLISH",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final JSONObject object = JSON.parseObject(response).getJSONObject("data");
                                BEAN.setPageIndex(object.getInteger("pageNum"))
                                        .setPageSize(object.getInteger("pageSize"))
                                        .setIsHasNextPage(object.getBoolean("hasNextPage"));
                                //设置Adapter
//                                mAdapter = MultipleRecyclerAdapter.create(CONVERTER.setJsonData(response));
                                final List<MultipleItemEntity> data =
                                        new PublishDataConverter().setJsonData(response).convert();
                                mAdapter = new PublishAdapter(data,DELEGATE);

                                mAdapter.setOnLoadMoreListener(publishRefreshHandler.this,RECYCLERVIEW);

                                RECYCLERVIEW.setAdapter(mAdapter);
                                BEAN.addIndex();
                                BEAN.addPageSize();
                                break;
                            default:
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
                            .url(URL)
                            .params("pageNum",index)
                            .params("status","")
//                            .params("pageSize",pageSize)
                            .success(new ISuccess() {
                                @Override
                                public void onSuccess(String response) {
                                    HigoLogger.json("paging", response);
                                    final int status = JSON.parseObject(response).getInteger("status");
                                    switch (status){
                                        case 0:
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
