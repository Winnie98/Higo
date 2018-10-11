package com.yhl.higo.ec.main.personal.wishlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.yhl.higo.app.Higo;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.detail.product.ProductDetailDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.util.log.HigoLogger;

/**
 * Created by Administrator on 2018/5/18/018.
 */

public class WishListClickListener extends SimpleClickListener {

    //声明一个实例
    private final HigoDelegate DELEGATE;

    private WishListClickListener(HigoDelegate delegate) {
        this.DELEGATE = delegate;
    }

    public  static WishListClickListener create(HigoDelegate delegate){
        return new WishListClickListener(delegate);
    }

    @Override
    public void onItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
        final MultipleItemEntity entity = (MultipleItemEntity) baseQuickAdapter.getData().get(position);
        final int productId = entity.getField(MultipleFields.PRODUCT_ID);
        final ProductDetailDelegate delegate  = ProductDetailDelegate.create(productId);
        DELEGATE.getSupportDelegate().start(delegate);
    }

    @Override
    public void onItemLongClick(final BaseQuickAdapter adapter, View view, final int position) {
        final MultipleItemEntity entity = (MultipleItemEntity) baseQuickAdapter.getData().get(position);
        final int wishlistId = entity.getField(MultipleFields.ID);

        AlertDialog.Builder dialog = new AlertDialog.Builder(DELEGATE.getContext());
        dialog.setMessage("确认要从心愿单中移出吗")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        RestClient.builder()
                                .url("user/wishlist/remove_wishlist.do")
                                .params("wishlistId",wishlistId)
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        HigoLogger.d("DELETE_ADDRESS",response);
                                        final int status = JSON.parseObject(response).getInteger("status");
                                        final String msg = JSON.parseObject(response).getString("msg");
                                        switch (status){
                                            case 0:
                                                adapter.remove(position);
                                                break;
                                            default:
                                                Toast.makeText(Higo.getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                                                break;
                                        }
                                    }
                                })
                                .build()
                                .post();

                    }
                })
                .setNegativeButton("取消",null)
                .show();

    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {

    }
}
