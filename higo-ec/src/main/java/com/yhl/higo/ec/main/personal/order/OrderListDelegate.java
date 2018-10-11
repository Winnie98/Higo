package com.yhl.higo.ec.main.personal.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.identify.IdentifyDelegate;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.ec.sign.SignInCodeDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.util.log.HigoLogger;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/20/020.
 */

public class OrderListDelegate extends HigoDelegate {

//    private String mType = null;
    private int mOrderStatusId ;
    private int mUserTypeId ;

    @BindView(R2.id.rv_order_list)
    RecyclerView mRecyclerView = null;
    @BindView(R2.id.swipe_refresh_purchase_all)
    SwipeRefreshLayout mSwipeRefreshLayout = null;
    @BindView(R2.id.txt_toolbar_order_name)
    TextView mToolBarName  =  null;
    @BindView(R2.id.spinner_user_type)
    Spinner mSpinner = null;

    private int mCurrentUserId;
    private OrderRefreshHandler mRefreshHandler = null;

    @OnClick(R2.id.icon_order_list_back)
    void onClickBack(){
        getSupportDelegate().pop();
    }


    @Override
    public Object setLayout() {
        return R.layout.delegate_order_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            mOrderStatusId = args.getInt("orderStatusId");
        }
    }


    public static OrderListDelegate create(int orderStatusId , int userTypeId){
        final Bundle args = new Bundle();
        args.putInt("orderStatusId", orderStatusId);
        args.putInt("userTypeId", userTypeId);
        final OrderListDelegate delegate = new OrderListDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    public static OrderListDelegate create(int orderStatusId){
        final Bundle args = new Bundle();
        args.putInt("orderStatusId", orderStatusId);
        final OrderListDelegate delegate = new OrderListDelegate();
        delegate.setArguments(args);
        return delegate;
    }


    private void setTitle(int orderStatusId){
        switch (orderStatusId){
            case 0:
                mToolBarName.setText("全部订单");
                break;
            case 1:
                mToolBarName.setText("待付款");
                break;
            case 2:
                mToolBarName.setText("待发货");
                break;
            case 3:
                mToolBarName.setText("待收货");
                break;
            case 4:
                mToolBarName.setText("已完成");
                break;
            default:
                break;
        }
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        setTitle(mOrderStatusId);
        initRefreshLayout();
        RestClient.builder()
                .url("user/user/get_current_user_info.do")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("USER_INFO",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                mCurrentUserId = JSON.parseObject(response).getJSONObject("data").getInteger("id");

                                //创建类型下拉框的数据源
                                String[] mTypeSpinnerData = new String[]{"全部", "代购","购买"};
                                //创建适配器(下拉框的数据源是来自适配器)
                                ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,mTypeSpinnerData);
                                //将适配器中的数据显示在Spinner上
                                mSpinner.setAdapter(typeAdapter);
                                mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        mUserTypeId  = position;
                                        mRefreshHandler =  OrderRefreshHandler.create(mSwipeRefreshLayout,mRecyclerView,new OrderListDataConverter(),OrderListDelegate.this,mOrderStatusId,mUserTypeId,mCurrentUserId);
                                        mRefreshHandler.orderFirstPage("user/order/get_order_list.do");
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
//                                mRefreshHandler =  OrderRefreshHandler.create(mSwipeRefreshLayout,mRecyclerView,new OrderListDataConverter(),OrderListDelegate.this,mOrderStatusId,mUserTypeId,mCurrentUserId);
//                                mRefreshHandler.myFeedbackFirstPage("user/order/get_order_list.do");
                                break;
                            case 3:
                                final String msg = JSON.parseObject(response).getString("msg");
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                getSupportDelegate().start(new SignInCodeDelegate());
                                getSupportDelegate().pop();
                            default:
                                break;
                        }
                    }
                })
                .build()
                .get();
    }

    private void initRefreshLayout(){
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        mSwipeRefreshLayout.setProgressViewOffset(true,120,300);
    }


    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);


    }

//    @Override
//    public void onSupportVisible() {
//        super.onSupportVisible();
//        mRefreshHandler.orderFirstPage("user/order/get_order_list.do");
//    }
}
