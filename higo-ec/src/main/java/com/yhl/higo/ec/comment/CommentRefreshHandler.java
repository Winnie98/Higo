package com.yhl.higo.ec.comment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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

import static com.blankj.utilcode.util.Utils.getContext;

/**
 * Created by Administrator on 2018/5/12/012.
 */

public class CommentRefreshHandler implements
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.RequestLoadMoreListener{

    private final SwipeRefreshLayout REFRESH_LAYOUT;
    private final PaginBean BEAN;
    private final RecyclerView RECYCLERVIEW;
    private CommentAdapter mAdapter = null;
    private final DataConverter CONVERTER;
    private int mPromoId = -1;
    private final HigoDelegate DELEGATE;

    private CommentRefreshHandler(SwipeRefreshLayout swipeRefreshLayout,
                                  RecyclerView recyclerView,
                                  DataConverter converter,
                                  PaginBean bean, HigoDelegate delegate) {
        this.REFRESH_LAYOUT = swipeRefreshLayout;
        this.RECYCLERVIEW = recyclerView;
        this.CONVERTER = converter;
        this.BEAN = bean;
        DELEGATE = delegate;
        REFRESH_LAYOUT.setOnRefreshListener(this);
    }

    public static CommentRefreshHandler create(SwipeRefreshLayout swipeRefreshLayout,
                                               RecyclerView recyclerView, DataConverter converter, HigoDelegate delegate) {
        return new CommentRefreshHandler(swipeRefreshLayout, recyclerView, converter, new PaginBean(), delegate);
    }

    private void refresh() {
        REFRESH_LAYOUT.setRefreshing(true);
        Higo.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //进行一些网络请求
                firstCommentPage(1, mPromoId);
                REFRESH_LAYOUT.setRefreshing(false);
            }
        }, 2000);
    }

    public void firstCommentPage(int pageNum, int promoId) {
        mPromoId = promoId;
        BEAN.setDelayed(1000);
        RestClient.builder()
                .url("user/comment/get_normal_comment_list.do")
                .params("pageNum", pageNum)
                .params("promoId", mPromoId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("COMMENT", response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status) {
                            case 0:
                                final JSONObject object = JSON.parseObject(response).getJSONObject("data");

                                BEAN.setPageIndex(object.getInteger("pageNum"))
                                        .setPageSize(object.getInteger("pageSize"))
                                        .setIsHasNextPage(object.getBoolean("hasNextPage"));

                                CONVERTER.clearData();

                                final List<MultipleItemEntity> data =
                                        new PromoCommentDataConverter().setJsonData(response).convert();


                                mAdapter = new CommentAdapter(data, DELEGATE);

                                mAdapter.setOnLoadMoreListener(CommentRefreshHandler.this, RECYCLERVIEW);

                                RECYCLERVIEW.setAdapter(mAdapter);
                                BEAN.addIndex();

                                BEAN.addPageSize();
                                break;

                            case 1:
                                final String msg = JSON.parseObject(response).getString("msg");
                                Toast.makeText(Higo.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
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

//        if (mAdapter.getData().size() < pageSize || currentCount >= total || !hasNextPage) {
        if (!hasNextPage) {
            mAdapter.loadMoreEnd(true);
            Toast.makeText(getContext(), "当前已是最后一页", Toast.LENGTH_SHORT).show();
        } else {
            Higo.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    RestClient.builder()
                            .url("user/comment/get_normal_comment_list.do")
                            .params("pageNum", index)
//                            .params("pageSize",pageSize)
                            .params("promoId", mPromoId)
                            .success(new ISuccess() {
                                @Override
                                public void onSuccess(String response) {
                                    HigoLogger.json("paging", response);
                                    final int status = JSON.parseObject(response).getInteger("status");
                                    switch (status) {
                                        case 0:
                                            final JSONObject object = JSON.parseObject(response).getJSONObject("data");
//                                            final LinearLayoutManager manager = new LinearLayoutManager(getContext());
//                                            RECYCLERVIEW.setLayoutManager(manager);

                                            BEAN.setPageIndex(object.getInteger("pageNum"))
                                                    .setPageSize(object.getInteger("pageSize"))
                                                    .setIsHasNextPage(object.getBoolean("hasNextPage"));

                                            mAdapter.addData(CONVERTER.setJsonData(response).convert());
                                            mAdapter.loadMoreComplete();

//                                            mAdapter.setOnLoadMoreListener(CommentRefreshHandler.this, RECYCLERVIEW);

//                                            RECYCLERVIEW.setAdapter(mAdapter);


                                            BEAN.addIndex();
                                            BEAN.addPageSize();
                                            break;
                                        case 1:
                                            final String msg = JSON.parseObject(response).getString("msg");
                                            Toast.makeText(Higo.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
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
