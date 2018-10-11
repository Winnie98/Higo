package com.yhl.higo.ec.main.market;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.detail.product.ProductDetailDelegate;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;

/**
 * Created by Administrator on 2018/5/18/018.
 */

public class MarketClickListener extends SimpleClickListener {

    //声明一个实例
    private final HigoDelegate DELEGATE;

    private MarketClickListener(HigoDelegate delegate) {
        this.DELEGATE = delegate;
    }

    public  static MarketClickListener create(HigoDelegate delegate){
        return new MarketClickListener(delegate);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        final MultipleItemEntity entity = (MultipleItemEntity) baseQuickAdapter.getData().get(position);
        final int productId = entity.getField(MultipleFields.ID);
        final ProductDetailDelegate delegate  = ProductDetailDelegate.create(productId);
        DELEGATE.getSupportDelegate().start(delegate);
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
}
