package com.yhl.higo.ec.main.sort.content;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.detail.promo.PromoDetailDelegate;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;

/**
 * Created by Administrator on 2018/5/18/018.
 */

public class ContentItemClickListener extends SimpleClickListener {

    //声明一个实例
    private final HigoDelegate DELEGATE;

    private ContentItemClickListener(HigoDelegate delegate) {
        this.DELEGATE = delegate;
    }

    public  static SimpleClickListener create(HigoDelegate delegate){
        return new ContentItemClickListener(delegate);
    }



    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        final MultipleItemEntity entity = (MultipleItemEntity) baseQuickAdapter.getData().get(position);
        final int promoId = entity.getField(MultipleFields.ID);
        final PromoDetailDelegate delegate  = PromoDetailDelegate.create(promoId);
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
