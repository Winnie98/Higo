package com.yhl.higo.ec.search.promo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/6/23/023.
 */

public class SearchPromoDelegate extends HigoDelegate {
    @BindView(R2.id.recycler_search_promo)
    RecyclerView mRecyclerView = null;
    @BindView(R2.id.swipe_search_promo)
    SwipeRefreshLayout mRefreshLayout = null;
    @BindView(R2.id.edit_search_promo)
    EditText mEditPromo = null;

    @OnClick(R2.id.img_search_promo)
    void onClickSearchPromo(){
        mRefreshHandler =  SearchPromoRefreshHandler.create(mRefreshLayout,mRecyclerView,new SearchPromoDataConverter(),"user/promo/search_passed_promo.do",mEditPromo.getText().toString(),this);
        mRefreshHandler.firstPage();
    }

    private SearchPromoRefreshHandler mRefreshHandler = null;



    private void initRefreshLayout(){
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
        mRefreshLayout.setProgressViewOffset(true,120,300);
    }

    private void initRecyclerView(){
//        final GridLayoutManager manager = new GridLayoutManager(getContext(),4);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);

//        mRecyclerView.addOnItemTouchListener(IndexItemClickListener.create(this));
    }
    @Override
    public Object setLayout() {
        return R.layout.delegate_search_promo;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        initRecyclerView();
        initRefreshLayout();
//        mRefreshHandler =  SearchPromoRefreshHandler.create(mRefreshLayout,mRecyclerView,new PublishDataConverter(),"user/promo/search_passed_promo.do",mEditPromo.getText().toString());
//        mRefreshHandler.myFeedbackFirstPage();
    }
}
