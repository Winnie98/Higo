package com.yhl.higo.ec.search.product;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.main.index.IndexDataConverter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/23/023.
 */

public class SearchProductDelegate extends HigoDelegate {
    @BindView(R2.id.recycler_search_product)
    RecyclerView mRecyclerView = null;
    @BindView(R2.id.swipe_search_product)
    SwipeRefreshLayout mRefreshLayout = null;
    @BindView(R2.id.edit_search_product)
    EditText mEditProduct = null;

    @BindView(R2.id.spinner_search_type)
    Spinner mTypeSpinner = null;

    private int mTypeId = -1;
    private final String URL = "user/product/search_normal_product.do";

    private SearchProductRefreshHandler mRefreshHandler = null;

    @OnClick(R2.id.img_search_product)
    void onClickSearchProduct(){
//        mRefreshHandler =  SearchProductRefreshHandler.create(mRefreshLayout,mRecyclerView,new IndexDataConverter(),URL, mTypeId, mEditProduct.getText().toString(),this);
        mRefreshHandler =  SearchProductRefreshHandler.create(mRefreshLayout,mRecyclerView,new SearchProductDataConverter(),URL, mTypeId, mEditProduct.getText().toString(),this);
        mRefreshHandler.firstPage();
    }




    private void initRefreshLayout(){
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        mRefreshLayout.setProgressViewOffset(true,120,300);
    }

    private void initRecyclerView(){
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);

//        mRecyclerView.addOnItemTouchListener(MarketClickListener.create(this));
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_search_product;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull final View rootView) {
        initRecyclerView();
        initRefreshLayout();

        initTypeData();

    }

    private void initTypeData(){
        //创建类型下拉框的数据源
        String[] mTypeSpinnerData = new String[]{"全部", "代购","求购"};
        //创建适配器(下拉框的数据源是来自适配器)
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,mTypeSpinnerData);
        //将适配器中的数据显示在Spinner上
        mTypeSpinner.setAdapter(typeAdapter);
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTypeId  = position;
                mRefreshHandler =  SearchProductRefreshHandler.create(mRefreshLayout,mRecyclerView,new IndexDataConverter(),URL, mTypeId, mEditProduct.getText().toString(),SearchProductDelegate.this);
                mRefreshHandler.firstPage();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
