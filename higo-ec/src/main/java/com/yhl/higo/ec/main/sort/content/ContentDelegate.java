package com.yhl.higo.ec.main.sort.content;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.main.EcBottomDelegate;
import com.yhl.higo.ec.main.personal.address.AddressAdapter;
import com.yhl.higo.ec.main.personal.address.AddressDataConverter;
import com.yhl.higo.ec.main.sort.SortDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.util.log.HigoLogger;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/5/18/018.
 */

public class ContentDelegate extends HigoDelegate implements ISuccess {

    private ContentRefreshHandler mRefreshHandler = null;
    private static final String ARG_CONTENT_ID = "CONTENT_ID";
    private int mContentId = -1;
    private List<SectionBean> mData = null;

    @BindView(R2.id.rv_list_content)
    RecyclerView mRecyclerView = null;

//    @BindView(R2.id.swip_content)
//    SwipeRefreshLayout mRefreshLayout = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null){
            mContentId = args.getInt(ARG_CONTENT_ID);
        }
    }

    public static ContentDelegate newInstance(int contentId){
        final Bundle args = new Bundle();
        args.putInt(ARG_CONTENT_ID,contentId);
        final ContentDelegate delegate = new ContentDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_list_content;
    }

    private void initData(){
        RestClient.builder()
                .url("user/promo/get_passed_promo_list.do")
                .params("categoryId",mContentId)
                .params("countryId",0)
                .success(this)
                .build()
                .get();
    }



    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        final StaggeredGridLayoutManager manager =
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);

//        initRefreshLayout();

//        final SortDelegate sortDelegate  = getParentDelegate();
//        final EcBottomDelegate ecBottomDelegate = sortDelegate.getParentDelegate();
//        mRecyclerView.addOnItemTouchListener(ContentItemClickListener.create(ecBottomDelegate));

        mRefreshHandler =  ContentRefreshHandler.create(mRecyclerView,ContentDelegate.this,new ContentDataConverter(),mContentId);

        mRefreshHandler.firstContentPage();
//        initData();
    }

    @Override
    public void onSuccess(String response) {
        HigoLogger.d("PRODUCT",response);
        final int status = JSON.parseObject(response).getInteger("status");
        switch (status) {
            case 0:
                final List<MultipleItemEntity> data =
                        new ContentDataConverter().setJsonData(response).convert();

                final ContentAdapter contentAdapter = new ContentAdapter(data,this);

                mRecyclerView.setAdapter(contentAdapter);
                break;
            case 1:
                break;
            default:
                break;
        }

    }
}
