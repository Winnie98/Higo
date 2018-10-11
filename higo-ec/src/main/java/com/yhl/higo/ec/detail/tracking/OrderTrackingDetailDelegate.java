package com.yhl.higo.ec.detail.tracking;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.IError;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.util.log.HigoLogger;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/9/6/006.
 */

public class OrderTrackingDetailDelegate extends HigoDelegate{
    private String mOrderTrackingNo;
    private OrderTrackingAdapter mAdapter = null;

    @BindView(R2.id.txt_shipperName)
    TextView mShipperName  = null;
    @BindView(R2.id.txt_trackingNo)
    TextView mTrackingNo = null;
    @BindView(R2.id.recycler_tracking)
    RecyclerView mRecyclerView = null;

    public static OrderTrackingDetailDelegate create(String trackingNo ){
        final Bundle args = new Bundle();
        args.putString("trackingNo", trackingNo);
        final OrderTrackingDetailDelegate delegate = new OrderTrackingDetailDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @OnClick(R2.id.tracking_toolbar)
    void onClickBack(){
        getSupportDelegate().pop();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            mOrderTrackingNo = args.getString("trackingNo");
        }
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_order_tracking_detail;
    }



    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        RestClient.builder()
                .url("common/express/trace.do")
                .params("trackingNo",mOrderTrackingNo)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("TRACKING_DETAIL",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final String shipperName = JSON.parseObject(response).getJSONObject("data").getString("shipperName");
                                mShipperName.setText(shipperName);
                                mTrackingNo.setText(mOrderTrackingNo);
                                final LinearLayoutManager manager =
                                        new LinearLayoutManager(getContext());
                                mRecyclerView.setLayoutManager(manager);

                                final List<MultipleItemEntity> data =
                                        new OrderTrackingDataConverter().setJsonData(response).convert();

                                mAdapter = new OrderTrackingAdapter(data);
                                mRecyclerView.setAdapter(mAdapter);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        HigoLogger.d("ERROR_CODE",code);
                        HigoLogger.d("TRACKING_DETAIL_ERROR_MSG",msg);
                    }
                })
                .build()
                .post();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }
}
