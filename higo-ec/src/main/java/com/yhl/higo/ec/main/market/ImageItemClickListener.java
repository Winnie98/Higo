package com.yhl.higo.ec.main.market;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;

/**
 * Created by Administrator on 2018/5/18/018.
 */

public class ImageItemClickListener extends SimpleClickListener {

    //声明一个实例
//    private final HigoDelegate DELEGATE;

    private EditProductDelegate DELEGATE;
//    private final String mImages;

//    private IndexImageItemClickListener(HigoDelegate delegate, String subImages, EditProductDelegate delegate1, String mImages) {
//        this.DELEGATE = delegate;
//        this.delegate = delegate1;
//        this.mImages = mImages;
//    }

//    private IndexImageItemClickListener(EditProductDelegate delegate1, String mImages) {
//        this.delegate = delegate1;
//        this.mImages = mImages;
//    }

    private ImageItemClickListener(EditProductDelegate delegate) {
        this.DELEGATE = delegate;
    }


    public  static ImageItemClickListener create(EditProductDelegate delegate){
        return new ImageItemClickListener(delegate);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemLongClick(final BaseQuickAdapter adapter,final View view,final int position) {
        final MultipleItemEntity entity = (MultipleItemEntity) baseQuickAdapter.getData().get(position);
//        mImages = delegate.mSubImages;
        final String singleImage = entity.getField(MultipleFields.SINGLE_IMAGE);

//        final List<String> images = new ArrayList<>();

//        String[] strArray = delegate.mSubImages.split(",");
//        final int size = strArray.length;
//        for (int i = 0; i < size; i++) {
//            if (singleImage.equals(strArray[i])){
//                if (i != size-1){
//                    delegate.mSubImages.replace(strArray[i]+",","");
//                }else {
//                    delegate.mSubImages.replace(","+strArray[i],"");
//                }
//
//            }
//        }


        AlertDialog.Builder dialog = new AlertDialog.Builder(DELEGATE.getContext());
        dialog.setMessage("确认要移出该图片吗")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String[] strArray = DELEGATE.mSubImages.split(",");
                        final int size = strArray.length;
                        for (int i = 0; i < size; i++) {
                            if (singleImage.equals(strArray[i])){
                                if (i != size-1){
                                    DELEGATE.mSubImages = DELEGATE.mSubImages.replace(strArray[i]+",","");
                                }else {
                                    DELEGATE.mSubImages = DELEGATE.mSubImages.replace(","+strArray[i],"");
                                }
                            }
                        }
                        adapter.remove(position);
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
