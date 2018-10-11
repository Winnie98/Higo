package com.yhl.higo.ec.main.personal.wishlist;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.joanzapata.iconify.widget.IconTextView;
import com.yhl.higo.app.Higo;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.detail.product.ProductDetailDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.image.GlideApp;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.ui.recycler.MultipleRecyclerAdapter;
import com.yhl.higo.ui.recycler.MultipleViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2018/5/22/022.
 */

public class WishListAdapter extends MultipleRecyclerAdapter {

    private final HigoDelegate DELEGATE;
    private WishListDataConverter mDataConverter = new WishListDataConverter();

    protected WishListAdapter(List<MultipleItemEntity> data, HigoDelegate delegate) {
        super(data);
        DELEGATE = delegate;
//        addItemType(ItemType.WISH_LIST, R.layout.item_wish_list);
        addItemType(ItemType.WISH_LIST, R.layout.item_product);
    }


    @Override
    protected void convert(final MultipleViewHolder holder, final MultipleItemEntity entity) {
        super.convert(holder, entity);
        switch (holder.getItemViewType()) {
            case ItemType.WISH_LIST:
                final String productImg = entity.getField(MultipleFields.IMAGE_URL);
                final String productName = entity.getField(MultipleFields.TITLE);
                final Double productPrice = entity.getField(MultipleFields.PRICE);
                final int id = entity.getField(MultipleFields.ID);
                final String categoryName = entity.getField(MultipleFields.CATEGORY);
                final String username = entity.getField(MultipleFields.USER_NAME);
                final int quantity = entity.getField(MultipleFields.NUMBER);
                final String typename = entity.getField(MultipleFields.PRODUCT_TYPE);
                final String createTime = entity.getField(MultipleFields.CREATE_TIME);
                final String countryFlag = entity.getField(MultipleFields.COUNTRY_IMG);
                final String countryName = entity.getField(MultipleFields.COUNTRY_NAME);

                final ImageView mImg = holder.getView(R.id.img_product);
                final TextView mName = holder.getView(R.id.txt_product_name);
                final TextView mPrice = holder.getView(R.id.txt_product_price);
                final Button mType = holder.getView(R.id.btn_product_type);
                final TextView mUser = holder.getView(R.id.txt_publish_to_market_username);
                final TextView mTime = holder.getView(R.id.createTime);
                final Button mCategory = holder.getView(R.id.btn_product_category);
                final ImageView mCountryImg = holder.getView(R.id.img_product_country);
                final TextView mCountry = holder.getView(R.id.txt_product_country);

                GlideApp.with(mContext)
                        .load(productImg)
                        .into(mImg);

                mName.setText(productName);
                mPrice.setText(String.valueOf(productPrice));
                mType.setText(typename);
                mUser.setText(username);
                mTime.setText(createTime);
                mCategory.setText(categoryName);

                GlideApp.with(mContext)
                        .load(countryFlag)
                        .into(mCountryImg);

                mCountry.setText(countryName);
                break;
            default:
                break;
        }

    }

}
