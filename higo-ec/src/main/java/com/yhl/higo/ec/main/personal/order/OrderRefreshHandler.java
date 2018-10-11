package com.yhl.higo.ec.main.personal.order;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter.RequestLoadMoreListener;
import com.yhl.higo.app.Higo;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.identify.IdentifyDelegate;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.ec.sign.SignInCodeDelegate;
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

public class OrderRefreshHandler implements
        SwipeRefreshLayout.OnRefreshListener,
        RequestLoadMoreListener,
        ISuccess
        {

    private final SwipeRefreshLayout REFRESH_LAYOUT;
    private final PaginBean BEAN;
    private final RecyclerView RECYCLERVIEW;
    private OrderListAdapter mAdapter  =  null;
    private final DataConverter CONVERTER;
    private final HigoDelegate DELEGATE;
    private final int ORDER_STATUS;
    private final int USER_TYPE;
    private final int CURRENT_ID ;

//    private final OrderListDelegate DELEGATE;

//    private int mOrderStatus = -1;
//    private int mUserType = -1;


//    private final OrderListDelegate DELEGATE = null;

    private OrderRefreshHandler(SwipeRefreshLayout swipeRefreshLayout,
                                RecyclerView recyclerView,
                                DataConverter converter,
                                PaginBean bean,
                                HigoDelegate delegate,
                                int order_status,
                                int user_type,
                                int currentUserId) {
        this.REFRESH_LAYOUT = swipeRefreshLayout;
        this.RECYCLERVIEW = recyclerView;
        this.CONVERTER = converter;
        this.BEAN = bean;
        this.DELEGATE = delegate;
        ORDER_STATUS = order_status;
        USER_TYPE = user_type;
        REFRESH_LAYOUT.setOnRefreshListener(this);
        this.CURRENT_ID = currentUserId;
    }

    public static OrderRefreshHandler create(SwipeRefreshLayout swipeRefreshLayout,
                                             RecyclerView recyclerView, DataConverter converter,
                                             HigoDelegate delegate,int order_status,
                                             int user_type, int currentUserId){
        return new OrderRefreshHandler(swipeRefreshLayout,recyclerView,converter,new PaginBean(),delegate,order_status,user_type,currentUserId);
    }

    private void refresh(){
        REFRESH_LAYOUT.setRefreshing(true);
        Higo.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //进行一些网络请求
                orderFirstPage("user/order/get_order_list.do");
                REFRESH_LAYOUT.setRefreshing(false);
            }
        },2000);
    }


    public void orderFirstPage(final String url){
        BEAN.setDelayed(1000);
//        RestClient.builder()
//                .url("user/order/get_order_list.do")
//                .params("pageNum",1)
//                .params("status", ORDER_STATUS)
//                .params("userType", USER_TYPE)
//                .success(this)
//                .build()
//                .get();
        if (ORDER_STATUS == 0 ){
            HigoLogger.d("ORDER_STATUS=0",ORDER_STATUS);
            HigoLogger.d("USER_TYPE",USER_TYPE);

            RestClient.builder()
//                    .loader(getContext())
                    .url(url)
                    .params("pageNum",1)
//                    .params("pageSize",10)
                    .params("status","")
                    .params("userType",USER_TYPE)
                    .success(this)
                    .build()
                    .get();
        }else if (ORDER_STATUS != 0 ) {
            HigoLogger.d("ORDER_STATUS!=0",ORDER_STATUS);
            HigoLogger.d("USER_TYPE",USER_TYPE);

            RestClient.builder()
                    .url("user/order/get_order_list.do")
                    .params("pageNum",1)
                    .params("status", ORDER_STATUS)
                    .params("userType", USER_TYPE)
                    .success(this)
                    .build()
                    .get();
        }

    }

    private void paging(final String url) {

        final int index = BEAN.getPageIndex();
        final int pageSize = BEAN.getPageSize();

        final boolean hasNextPage = BEAN.getIsHasNextPage();

        if (!hasNextPage) {
            mAdapter.loadMoreEnd(true);
            Toast.makeText(getContext(),"当前已是最后一页",Toast.LENGTH_SHORT).show();
        } else {
            Higo.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ORDER_STATUS == 0 ){
                        HigoLogger.d("ORDER_STATUS=0",ORDER_STATUS);
                        HigoLogger.d("USER_TYPE",USER_TYPE);

                        RestClient.builder()
//                    .loader(getContext())
                                .url(url)
                                .params("pageNum",1)
//                    .params("pageSize",10)
                                .params("status","")
                                .params("userType",USER_TYPE)
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        HigoLogger.json("paging", response);
                                        final int status = JSON.parseObject(response).getInteger("status");
                                        switch (status) {
                                            case 0:
                                                final JSONObject object = JSON.parseObject(response).getJSONObject("data");

                                                BEAN.setPageIndex(object.getInteger("pageNum"))
                                                        .setPageSize(object.getInteger("pageSize"))
                                                        .setIsHasNextPage(object.getBoolean("hasNextPage"));

                                                final List<MultipleItemEntity> data =
                                                        new OrderListDataConverter().setJsonData(response).convert();
                                                mAdapter.addData(data);
                                                mAdapter.loadMoreComplete();

                                                BEAN.addIndex();
                                                BEAN.addPageSize();
                                                break;
                                            case 1:
                                                final List<MultipleItemEntity> data1 =
                                                        new OrderListDataConverter().setJsonData(response).convert();
                                                //设置Adapter
                                                mAdapter = new OrderListAdapter(data1,DELEGATE,CURRENT_ID,status);
                                                mAdapter.setOnLoadMoreListener(OrderRefreshHandler.this,RECYCLERVIEW);

                                                RECYCLERVIEW.setAdapter(mAdapter);
                                                break;
                                            case 3:
                                                final String msg = JSON.parseObject(response).getString("msg");
                                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();

                                                DELEGATE.getSupportDelegate().start(new SignInCodeDelegate());
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                })
                                .build()
                                .get();
                    }else if (ORDER_STATUS != 0 ) {
                        HigoLogger.d("ORDER_STATUS!=0",ORDER_STATUS);
                        HigoLogger.d("USER_TYPE",USER_TYPE);

                        RestClient.builder()
                                .url("user/order/get_order_list.do")
                                .params("pageNum",1)
                                .params("status", ORDER_STATUS)
                                .params("userType", USER_TYPE)
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        HigoLogger.json("paging", response);
                                        final int status = JSON.parseObject(response).getInteger("status");
                                        switch (status) {
                                            case 0:
                                                final JSONObject object = JSON.parseObject(response).getJSONObject("data");

                                                BEAN.setPageIndex(object.getInteger("pageNum"))
                                                        .setPageSize(object.getInteger("pageSize"))
                                                        .setIsHasNextPage(object.getBoolean("hasNextPage"));

                                                final List<MultipleItemEntity> data =
                                                        new OrderListDataConverter().setJsonData(response).convert();
                                                mAdapter.addData(data);
                                                mAdapter.loadMoreComplete();

                                                BEAN.addIndex();
                                                BEAN.addPageSize();
                                                break;
                                            case 1:
                                                final List<MultipleItemEntity> data1 =
                                                        new OrderListDataConverter().setJsonData(response).convert();
                                                //设置Adapter
                                                mAdapter = new OrderListAdapter(data1,DELEGATE,CURRENT_ID,status);
                                                mAdapter.setOnLoadMoreListener(OrderRefreshHandler.this,RECYCLERVIEW);

                                                RECYCLERVIEW.setAdapter(mAdapter);
                                                break;
                                            case 3:
                                                final String msg = JSON.parseObject(response).getString("msg");
                                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();

                                                DELEGATE.getSupportDelegate().start(new SignInCodeDelegate());
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
        paging("user/order/get_order_list.do");
    }

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

                final LinearLayoutManager manager = new LinearLayoutManager(getContext());
                RECYCLERVIEW.setLayoutManager(manager);

                CONVERTER.clearData();
                final List<MultipleItemEntity> data =
                        new OrderListDataConverter().setJsonData(response).convert();

                //设置Adapter
                mAdapter = new OrderListAdapter(data,DELEGATE,CURRENT_ID,status);


                mAdapter.setOnLoadMoreListener(OrderRefreshHandler.this,RECYCLERVIEW);

                RECYCLERVIEW.setAdapter(mAdapter);
                BEAN.addIndex();
                BEAN.addPageSize();
                break;
            case 1:
                final LinearLayoutManager manager1 = new LinearLayoutManager(getContext());
                RECYCLERVIEW.setLayoutManager(manager1);
                final List<MultipleItemEntity> data1 =
                        new OrderListDataConverter().setJsonData(response).convert();
                //设置Adapter
                mAdapter = new OrderListAdapter(data1,DELEGATE,CURRENT_ID,status);

                mAdapter.setOnLoadMoreListener(OrderRefreshHandler.this,RECYCLERVIEW);

                RECYCLERVIEW.setAdapter(mAdapter);
                break;
            case 3:
                final String msg = JSON.parseObject(response).getString("msg");
                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                DELEGATE.getSupportDelegate().start(new SignInCodeDelegate());
                break;
            default:
                break;
                }

            }
        }
