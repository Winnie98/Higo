package com.yhl.higo.ec.main.personal.address;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.identify.IdentifyDelegate;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.ec.main.personal.order.OrderConfirmDelegate;
import com.yhl.higo.ec.sign.SignInDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.util.log.HigoLogger;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/26/026.
 */

public class SelectAddressDelegate extends HigoDelegate implements ISuccess {

    @BindView(R2.id.swipe_refresh_select_address)
    SwipeRefreshLayout mSwipeRefreshLayout = null;

    @BindView(R2.id.rv_select_address)
    RecyclerView mRecyclerView = null;

    @OnClick(R2.id.icon_address_manage)
    void onClickAddAddress(){
        getSupportDelegate().start(new AddressDelegate());
    }

    @OnClick(R2.id.icon_select_address_back)
    void onClickBack(){
        getSupportDelegate().pop();
    }

    @OnClick(R2.id.btn_add_new_address)
    void onClickCreateAddress(){
        getSupportDelegate().start(new CreateAddressDelegate());
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_select_address;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);


    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        initAddress();

        initRefreshLayout();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initAddress();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mRecyclerView.addOnItemTouchListener(new SimpleClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                Toast.makeText(getContext(),"你点击了地址",Toast.LENGTH_SHORT).show();
                final MultipleItemEntity entity = (MultipleItemEntity) baseQuickAdapter.getData().get(position);
                final int addressId = entity.getField(MultipleFields.ID);
                final String name = entity.getField(MultipleFields.NAME);
                final String phone = entity.getField(MultipleFields.PHONE);
                final String address = entity.getField(MultipleFields.ADDRESS);
                Bundle bundle = new Bundle();
                bundle.putInt("addressId",addressId);
                bundle.putString("name", name);
                bundle.putString("phone", phone);
                bundle.putString("address", address);
                getSupportDelegate().setFragmentResult(RESULT_OK, bundle);

                getSupportDelegate().onDestroyView();
                getSupportDelegate().onDestroy();
                getSupportDelegate().pop();
            }

            @Override
            public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
    }

    private void initRefreshLayout(){
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        mSwipeRefreshLayout.setProgressViewOffset(true,120,300);
    }

    //访问服务器获得收货地址信息
    public void initAddress(){
        RestClient.builder()
                .url("user/address/get_address_list.do")
                .success(this)
                .build()
                .get();
    }



    @Override
    public void onSuccess(String response) {
        HigoLogger.d("ADDRESS_INFO",response);
        final int status = JSON.parseObject(response).getInteger("status");
        switch (status){
            case 0:
                final LinearLayoutManager manager = new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(manager);
                final List<MultipleItemEntity> data =
                        new AddressDataConverter().setJsonData(response).convert();

                final SelectAddressAdapter selectAddressAdapter = new SelectAddressAdapter(data);

                mRecyclerView.setAdapter(selectAddressAdapter);

                mSwipeRefreshLayout.setRefreshing(false);
                break;
            case 1:
                final String message = JSON.parseObject(response).getString("msg");
                Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();

                mSwipeRefreshLayout.setRefreshing(false);
                break;
            default:
                final String msg = JSON.parseObject(response).getString("msg");
                Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();

//                getSupportDelegate().start(new SignInDelegate());
                getSupportDelegate().start(IdentifyDelegate.create(IdentifyItemType.IDENTIFY_SIGN_IN));
                mSwipeRefreshLayout.setRefreshing(false);
                break;
        }
    }
}
