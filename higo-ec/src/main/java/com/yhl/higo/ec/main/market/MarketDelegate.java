package com.yhl.higo.ec.main.market;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yhl.higo.delegates.bottom.BottomItemDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.search.product.SearchProductDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.util.log.HigoLogger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/11/011.
 */

public class MarketDelegate extends BottomItemDelegate {

    @BindView(R2.id.recycler_market)
    RecyclerView mRecyclerView = null;
    @BindView(R2.id.swipe_market_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout = null;

    private int mTypeId  = 0;
    private int mCategoryId = 0;
    private String  mOrderBy = "time_desc";
    private int mCountryId = 0;

    private Integer[] mCategorySpinnerID = new Integer[]{};
    private String[] mCategorySpinnerData = new String[]{};

    private Integer[] mCountrySpinnerID = new Integer[]{};
    private String[] mCountrySpinnerData = new String[]{};

    private MarketRefreshHandler mRefreshHandler = null;

    @OnClick(R2.id.img_search_product)
    void onClickSearchProduct(){
        getParentDelegate().getSupportDelegate().start(new SearchProductDelegate());
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_market;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull final View rootView) {
        //获取分类列表
        RestClient.builder()
                .url("user/category/get_enabled_category_list.do")
                .loader(getContext())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("CATEGORY_LIST",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final JSONArray dataArray = JSON.parseObject(response).getJSONArray("data");

                                final List<Integer> categoryId = new ArrayList<>();
                                final List<String> categoryName = new ArrayList<>();
                                categoryId.add(0);
                                categoryName.add("全部");
                                final int size = dataArray.size();
                                for (int i = 0; i < size; i++) {
                                    final JSONObject data = dataArray.getJSONObject(i);
                                    final int id = data.getInteger("id");
                                    final String name = data.getString("name");
                                    categoryId.add(id);
                                    categoryName.add(name);
                                }
                                mCategorySpinnerID = categoryId.toArray(new Integer[categoryId.size()]);
                                mCategorySpinnerData = categoryName.toArray(new String[categoryName.size()]);

                                //创建类别下拉框的数据源
//                                for(String name : mCategorySpinnerData) {
//                                    HigoLogger.d("NAME",name);
//                                }
                                Spinner mCategorySpinner = ButterKnife.findById(rootView, R.id.spinner_product_category);
                                //创建适配器(下拉框的数据源是来自适配器)
                                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,mCategorySpinnerData);
                                //将适配器中的数据显示在Spinner上
                                mCategorySpinner.setAdapter(categoryAdapter);
                                mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        mCategoryId = mCategorySpinnerID[position];
                                        initData();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });


                                break;
                            default:
                                break;
                        }
                    }
                })
                .build()
                .get();
        //获取国家列表
        RestClient.builder()
                .url("user/country/get_enabled_country_list.do")
                .loader(getContext())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("COUNTRY_LIST",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final JSONArray dataArray = JSON.parseObject(response).getJSONArray("data");

                                final List<Integer> categoryId = new ArrayList<>();
                                final List<String> categoryName = new ArrayList<>();
                                categoryId.add(0);
                                categoryName.add("全部");
                                final int size = dataArray.size();
                                for (int i = 0; i < size; i++) {
                                    final JSONObject data = dataArray.getJSONObject(i);
                                    final int id = data.getInteger("id");
                                    final String name = data.getString("name");
                                    categoryId.add(id);
                                    categoryName.add(name);
                                }
                                mCountrySpinnerID = categoryId.toArray(new Integer[categoryId.size()]);
                                mCountrySpinnerData = categoryName.toArray(new String[categoryName.size()]);

                                //创建国家下拉框的数据源
//                                for(String name : mCountrySpinnerData) {
//                                    HigoLogger.d("NAME",name);
//                                }
                                Spinner mCountrySpinner = ButterKnife.findById(rootView, R.id.spinner_product_country);
                                //创建适配器(下拉框的数据源是来自适配器)
                                ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,mCountrySpinnerData);
                                //将适配器中的数据显示在Spinner上
                                mCountrySpinner.setAdapter(countryAdapter);
                                mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        mCountryId = mCountrySpinnerID[position];
                                        initData();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });


                                break;
                            default:
                                break;
                        }
                    }
                })
                .build()
                .get();

        Spinner mOrderSpinner = ButterKnife.findById(rootView, R.id.spinner_product_order_by);
        Spinner mTypeSpinner = ButterKnife.findById(rootView, R.id.spinner_product_type);

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
                initData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] mOrderSpinnerData = new String[]{ "按时间降序","按时间升序", "按价格降序", "按价格升序"};
        //创建适配器(下拉框的数据源是来自适配器)
        ArrayAdapter<String> orderAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,mOrderSpinnerData);
        //将适配器中的数据显示在Spinner上
        mOrderSpinner.setAdapter(orderAdapter);
        mOrderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        mOrderBy = "id_desc";
                        initData();
                        break;
                    case 1:
                        mOrderBy = "id_asc";
                        initData();
                        break;
                    case 2:
                        mOrderBy = "price_desc";
                        initData();
                        break;
                    case 3:
                        mOrderBy = "price_asc";
                        initData();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initData();
        initRefreshLayout();
        initRecyclerView();

//        final EcBottomDelegate ecBottomDelegate  = getParentDelegate();
//        mRecyclerView.addOnItemTouchListener(MarketClickListener.create(ecBottomDelegate));
    }

    //访问服务器获得信息
    private void initData() {
        mRefreshHandler = MarketRefreshHandler.create(mSwipeRefreshLayout, mRecyclerView, new MarketDataConverter(), MarketDelegate.this, mTypeId, mCategoryId, mOrderBy,mCountryId);
        mRefreshHandler.firstProductPage();
    }

    private void initRefreshLayout(){
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        mSwipeRefreshLayout.setProgressViewOffset(true,120,300);
    }

    private void initRecyclerView(){
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);

//        mRecyclerView.addItemDecoration(BaseDecoration.
//                create(ContextCompat.getColor(getContext(),R.color.app_background),5));
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        mRefreshHandler.firstProductPage();
    }
}
