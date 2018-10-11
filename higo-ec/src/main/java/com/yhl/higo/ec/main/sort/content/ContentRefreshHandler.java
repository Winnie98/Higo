package com.yhl.higo.ec.main.sort.content;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yhl.higo.app.Higo;
import com.yhl.higo.ec.main.index.IndexDataConverter;
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

public class ContentRefreshHandler implements
//        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener
        {

    private final PaginBean BEAN;
    private final RecyclerView RECYCLERVIEW;
    private final ContentDelegate DELEGATE;
    private ContentAdapter mAdapter  =  null;
    private final DataConverter CONVERTER;
    private final int mContentId;


    ContentRefreshHandler(
                          RecyclerView recyclerView,
                          ContentDelegate delegate, DataConverter converter,
                          PaginBean bean, int mContentId) {
        this.RECYCLERVIEW = recyclerView;
        DELEGATE = delegate;
        this.CONVERTER = converter;
        this.BEAN = bean;
        this.mContentId = mContentId;

//        REFRESH_LAYOUT.setOnRefreshListener(this);
    }

    public static ContentRefreshHandler create(
                                               RecyclerView recyclerView, ContentDelegate delegate, DataConverter converter , int contentId){
        return new ContentRefreshHandler(recyclerView, delegate, converter,new PaginBean(), contentId);
    }

//    private void refresh(){
//        REFRESH_LAYOUT.setRefreshing(true);
//        Higo.getHandler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //进行一些网络请求
////                CONVERTER.clearData();
////                myFeedbackFirstPage("user/promo/get_passed_promo_list.do");
////                mData.clear();
////                initBanner();
////                delegate.initBanner();
//                firstContentPage();
//                REFRESH_LAYOUT.setRefreshing(false);
//            }
//        },2000);
//    }



    public void firstContentPage(){
        RestClient.builder()
                .url("user/promo/get_passed_promo_list.do")
                .params("pageNum",1)
//                .params("pageSize",10)
                .params("categoryId",mContentId)
                .params("countryId",0)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("CONTENT",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final JSONObject object = JSON.parseObject(response).getJSONObject("data");
                                BEAN.setPageIndex(object.getInteger("pageNum"))
                                        .setPageSize(object.getInteger("pageSize"))
                                        .setIsHasNextPage(object.getBoolean("hasNextPage"));

                                final List<MultipleItemEntity> data =
                                        new ContentDataConverter().setJsonData(response).convert();

                                mAdapter = new ContentAdapter(data,DELEGATE);

                                mAdapter.setOnLoadMoreListener(ContentRefreshHandler.this,RECYCLERVIEW);
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
                            .url("user/promo/get_passed_promo_list.do")
                            .params("pageNum",index)
                            .params("categoryId",mContentId)
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


//    @Override
//    public void onRefresh() {
////        refresh();
//        REFRESH_LAYOUT.setRefreshing(false);
//    }

    @Override
    public void onLoadMoreRequested() {
        paging();
    }
}
