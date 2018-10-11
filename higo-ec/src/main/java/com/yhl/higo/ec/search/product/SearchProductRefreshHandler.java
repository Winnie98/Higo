package com.yhl.higo.ec.search.product;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yhl.higo.app.Higo;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.recycler.DataConverter;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.ui.refresh.PaginBean;
import com.yhl.higo.util.log.HigoLogger;

import java.util.List;

/**
 * Created by Administrator on 2018/5/12/012.
 */

public class SearchProductRefreshHandler implements
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener
        {

    private final SwipeRefreshLayout REFRESH_LAYOUT;
    private final PaginBean BEAN;
    private final RecyclerView RECYCLERVIEW;
//    private MarketAdapter mAdapter  =  null;
    private final DataConverter CONVERTER;
    private final String URL;
    private final int PRODUCT_TYPE;
    private final String KEY_WORD;
    private final HigoDelegate DELEGATE;

    private SearchProductAdapter mAdapter  =  null;

    private SearchProductRefreshHandler(SwipeRefreshLayout swipeRefreshLayout,
                                        RecyclerView recyclerView,
                                        DataConverter converter,
                                        PaginBean bean, String url,
                                        int productType, String keyWord,
                                        HigoDelegate delegate) {
        this.REFRESH_LAYOUT = swipeRefreshLayout;
        this.RECYCLERVIEW = recyclerView;
        this.CONVERTER = converter;
        this.BEAN = bean;
        URL = url;
        this.PRODUCT_TYPE = productType;
        this.KEY_WORD = keyWord;
        DELEGATE = delegate;
        REFRESH_LAYOUT.setOnRefreshListener(this);
    }

    public static SearchProductRefreshHandler create(SwipeRefreshLayout swipeRefreshLayout,
                                                     RecyclerView recyclerView, DataConverter converter,
                                                     String url, int productType, String keyWord, HigoDelegate delegate){
        return new SearchProductRefreshHandler(swipeRefreshLayout,recyclerView,converter,new PaginBean(), url,productType , keyWord, delegate);
    }

    private void refresh(){
        REFRESH_LAYOUT.setRefreshing(true);
        Higo.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //进行一些网络请求
//                myFeedbackFirstPage("user/promo/get_passed_promo_list.do");
                firstPage();
                REFRESH_LAYOUT.setRefreshing(false);
            }
        },2000);
    }

    public void firstPage(){
        BEAN.setDelayed(1000);
        if (PRODUCT_TYPE == 0){
            RestClient.builder()
                    .url(URL)
                    .params("pageNum",1)
                    .params("pageSize",10)
                    .params("productType","")
                    .params("keyword",KEY_WORD)
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            HigoLogger.d("SEARCH_PRODUCT",response);
                            final int status = JSON.parseObject(response).getInteger("status");
                            switch (status){
                                case 0:
                                    final JSONObject object = JSON.parseObject(response).getJSONObject("data");
                                    BEAN.setPageIndex(object.getInteger("pageNum"))
                                            .setPageSize(object.getInteger("pageSize"))
                                            .setIsHasNextPage(object.getBoolean("hasNextPage"));
                                    //设置Adapter
//                                    final List<MultipleItemEntity> data =
//                                            new MarketDataConverter().setJsonData(response).convert();
                                    final List<MultipleItemEntity> data =
                                            new SearchProductDataConverter().setJsonData(response).convert();

//                                    mAdapter = new MarketAdapter(data,DELEGATE);
                                    mAdapter = new SearchProductAdapter(data,DELEGATE);
//                                    mAdapter.notifyDataSetChanged();
                                    mAdapter.setOnLoadMoreListener(SearchProductRefreshHandler.this,RECYCLERVIEW);
                                    RECYCLERVIEW.setAdapter(mAdapter);

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
        }else {
            RestClient.builder()
                    .url(URL)
                    .params("pageNum",1)
                    .params("pageSize",10)
                    .params("productType",PRODUCT_TYPE)
                    .params("keyword",KEY_WORD)
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            HigoLogger.d("SEARCH_PRODUCT",response);
                            final int status = JSON.parseObject(response).getInteger("status");
                            switch (status){
                                case 0:
                                    final JSONObject object = JSON.parseObject(response).getJSONObject("data");
                                    BEAN.setPageIndex(object.getInteger("pageNum"))
                                            .setPageSize(object.getInteger("pageSize"))
                                            .setIsHasNextPage(object.getBoolean("hasNextPage"));
                                    //设置Adapter
//                                    final List<MultipleItemEntity> data =
//                                            new MarketDataConverter().setJsonData(response).convert();

                                    final List<MultipleItemEntity> data =
                                            new SearchProductDataConverter().setJsonData(response).convert();

//                                    mAdapter = new MarketAdapter(data,DELEGATE);
//                                    mAdapter.notifyDataSetChanged();
                                    mAdapter = new SearchProductAdapter(data,DELEGATE);
                                    mAdapter.setOnLoadMoreListener(SearchProductRefreshHandler.this,RECYCLERVIEW);
                                    RECYCLERVIEW.setAdapter(mAdapter);

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
                    if (PRODUCT_TYPE ==0){
                        RestClient.builder()
                                .url(URL)
                                .params("pageNum",index)
                                .params("productType","")
                                .params("keyword",KEY_WORD)
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
//                                                final List<MultipleItemEntity> data =
//                                                        new MarketDataConverter().setJsonData(response).convert();


                                                mAdapter.addData(CONVERTER.setJsonData(response).convert());
                                                mAdapter.loadMoreComplete();
//                                                mAdapter = new MarketAdapter(data,DELEGATE);
//                                                mAdapter.notifyDataSetChanged();
//                                                mAdapter.setOnLoadMoreListener(SearchProductRefreshHandler.this,RECYCLERVIEW);
//                                                RECYCLERVIEW.setAdapter(mAdapter);

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
                    }else {
                        RestClient.builder()
                                .url(URL)
                                .params("pageNum",index)
                                .params("productType",PRODUCT_TYPE)
                                .params("keyword",KEY_WORD)
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
//                                                final List<MultipleItemEntity> data =
//                                                        new MarketDataConverter().setJsonData(response).convert();
//
//                                                mAdapter.loadMoreComplete();
//                                                mAdapter = new MarketAdapter(data,DELEGATE);

                                                mAdapter.addData(CONVERTER.setJsonData(response).convert());
                                                mAdapter.loadMoreComplete();
//                                                mAdapter.setOnLoadMoreListener(SearchProductRefreshHandler.this,RECYCLERVIEW);
                                                RECYCLERVIEW.setAdapter(mAdapter);

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
