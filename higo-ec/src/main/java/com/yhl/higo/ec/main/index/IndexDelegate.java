package com.yhl.higo.ec.main.index;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.yhl.higo.delegates.bottom.BottomItemDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.search.promo.SearchPromoDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.banner.HolderCreator;
import com.yhl.higo.ui.recycler.BaseDecoration;
import com.yhl.higo.util.log.HigoLogger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by Administrator on 2018/5/12/012.
 */

public class IndexDelegate extends BottomItemDelegate
//        implements AppBarLayout.OnOffsetChangedListener
{

    @BindView(R2.id.rv_index)
    RecyclerView  mRecyclerView = null;
    @BindView(R2.id.srl_index)
    SwipeRefreshLayout mRefreshLayout = null;

//    @BindView(R2.id.tb_index)
//    Toolbar mToolbar = null;
    @BindView(R2.id.index_banner)
    ConvenientBanner<String> mIndexBanner = null;


    private IndexRefreshHandler mRefreshHandler = null;

    @OnClick(R2.id.img_index_add_promo)
    void onClickAddPromo(){
        getParentDelegate().getSupportDelegate().start(new PromoPublishDelegate());
    }

    @OnClick(R2.id.img_index_search)
    void onClickSearch(){
        getParentDelegate().getSupportDelegate().start(new SearchPromoDelegate());
    }


    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        mRecyclerView.setNestedScrollingEnabled(false);

        mRefreshHandler =  IndexRefreshHandler.create(mRefreshLayout,mRecyclerView,IndexDelegate.this,new IndexDataConverter(),"user/promo/get_passed_promo_list.do",mIndexBanner);
        initRefreshLayout();
        initBanner();
        initRecyclerView();

        mRefreshHandler.firstIndexPage();
    }

    public void initBanner() {
        RestClient.builder()
                .url("user/banner/get_banner.do")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("INDEX_BANNER",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final String array = JSON.parseObject(response).getJSONObject("data").getString("images");
                                final List<String> images = new ArrayList<>();
                                String[] strArray = array.split(",");
                                final int size = strArray.length;
                                for (int i = 0; i < size; i++) {
                                    images.add("http://img.higo.party/"+strArray[i]);
                                }
                                if (images.size() !=0){
                                    mIndexBanner
                                            .setPages(new HolderCreator(), images)
                                            .setPageIndicator(new int[]{R.drawable.dot_normal, R.drawable.dot_focus})
                                            .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                                            .setPageTransformer(new DefaultTransformer())
                                            .startTurning(3000)
                                            .setCanLoop(true);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                })
                .build()
                .get();

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
        mRecyclerView.addItemDecoration(BaseDecoration.
                create(ContextCompat.getColor(getContext(),R.color.app_background),5));
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);


    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_index;
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        mRefreshHandler.firstIndexPage();
    }
}
