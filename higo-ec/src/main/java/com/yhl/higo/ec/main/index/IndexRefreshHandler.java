package com.yhl.higo.ec.main.index;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yhl.higo.app.Higo;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.banner.HolderCreator;
import com.yhl.higo.ui.recycler.DataConverter;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.ui.refresh.PaginBean;
import com.yhl.higo.util.log.HigoLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/12/012.
 */

public class IndexRefreshHandler implements
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener
        {

    private final SwipeRefreshLayout REFRESH_LAYOUT;
    private final PaginBean BEAN;
    private final RecyclerView RECYCLERVIEW;
    private final HigoDelegate DELEGATE;
    private IndexAdapter mAdapter  =  null;
    private final DataConverter CONVERTER;
    private final String URL;
    private final ConvenientBanner<String> BANNER;

    IndexRefreshHandler(SwipeRefreshLayout swipeRefreshLayout,
                        RecyclerView recyclerView,
                        HigoDelegate delegate, DataConverter converter,
                        PaginBean bean, String url, ConvenientBanner<String> banner) {
        this.REFRESH_LAYOUT = swipeRefreshLayout;
        this.RECYCLERVIEW = recyclerView;
        DELEGATE = delegate;
        this.CONVERTER = converter;
        this.BEAN = bean;
        URL = url;
        BANNER = banner;
        REFRESH_LAYOUT.setOnRefreshListener(this);
    }

    public static IndexRefreshHandler create(SwipeRefreshLayout swipeRefreshLayout,
                                             RecyclerView recyclerView,HigoDelegate delegate, DataConverter converter,
                                             String url ,ConvenientBanner<String> banner){
        return new IndexRefreshHandler(swipeRefreshLayout,recyclerView, delegate, converter,new PaginBean(), url ,banner);
    }

    private void refresh(){
        REFRESH_LAYOUT.setRefreshing(true);
        Higo.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //进行一些网络请求
                initBanner();
                firstIndexPage();
                REFRESH_LAYOUT.setRefreshing(false);
            }
        },2000);
    }

    public void initBanner() {
                RestClient.builder()
                        .url("user/banner/get_banner.do")
                        .success(new ISuccess() {
                            @Override
                            public void onSuccess(String response) {
                                HigoLogger.d("INDEX_BANNER",response);
                                final int status = JSON.parseObject(response).getInteger("status");
                                switch (status){
                                    case 0:
                                        final String array = JSON.parseObject(response).getJSONObject("data").getString("images");
                                        final List<String> images = new ArrayList<>();
                                        String[] strArray = array.split(",");
                                        final int size = strArray.length;
                                        for (int i = 0; i < size; i++) {
                                            images.add("http://img.higo.party/"+strArray[i]);
                                        }
                                        if (images.size() !=0){
                                            BANNER
                                                    .setPages(new HolderCreator(), images)
                                                    .setPageIndicator(new int[]{R.drawable.dot_normal, R.drawable.dot_focus})
                                                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                                                    .setPageTransformer(new DefaultTransformer())
                                                    .startTurning(3000)
                                                    .setCanLoop(true);
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .build()
                        .get();

            }


    public void firstIndexPage(){
//        BEAN.setDelayed(1000);
        RestClient.builder()
                .url(URL)
                .params("pageNum",1)
                .params("pageSize",10)
                .params("categoryId",0)
                .params("countryId",0)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("INDEX",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final JSONObject object = JSON.parseObject(response).getJSONObject("data");
                                BEAN.setPageIndex(object.getInteger("pageNum"))
                                        .setPageSize(object.getInteger("pageSize"))
                                        .setIsHasNextPage(object.getBoolean("hasNextPage"));

                                final List<MultipleItemEntity> data =
                                        new IndexDataConverter().setJsonData(response).convert();

                                mAdapter = new IndexAdapter(data,DELEGATE);

                                mAdapter.setOnLoadMoreListener(IndexRefreshHandler.this,RECYCLERVIEW);

                                RECYCLERVIEW.setAdapter(mAdapter);
                                BEAN.addIndex();
                                BEAN.addPageSize();
                                break;

                            case 1:;
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
                            .params("categoryId",0)
                            .params("countryId",0)
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

                                            mAdapter.addData(CONVERTER.setJsonData(response).convert());

                                            mAdapter.loadMoreComplete();
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
