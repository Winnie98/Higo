package com.yhl.higo.ec.main.market;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ui.image.GlideApp;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.ui.recycler.MultipleRecyclerAdapter;
import com.yhl.higo.ui.recycler.MultipleViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2018/5/26/026.
 */

public class ProductImageAdapter extends MultipleRecyclerAdapter{

    private final HigoDelegate DELEGATE;

    //设置图片加载策略
    private static final RequestOptions RECYCLER_OPTIONS =
            new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate();



    public ProductImageAdapter(List<MultipleItemEntity> data, HigoDelegate delegate) {
        super(data);
        DELEGATE = delegate;
        addItemType(ItemType.IMAGE, R.layout.item_image);
//        addItemType(ItemType.PRODUCT_NO,R.layout.item_no_product);
    }

    @Override
    protected void convert(final MultipleViewHolder holder, MultipleItemEntity entity) {
        super.convert(holder, entity);
        switch (holder.getItemViewType()) {
            case ItemType.IMAGE:
                final String imgUrl  = entity.getField(MultipleFields.IMAGE_URL);

                final ImageView img = holder.getView(R.id.img_product_edit);

                Glide.with(mContext)
                        .load(imgUrl)
                        .apply(RECYCLER_OPTIONS)
                        .into(img);
//                        .into((ImageView) holder.getView(R.id.img_product_edit));

                break;
            default:
                break;
        }
    }

}
