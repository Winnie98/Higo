package com.yhl.higo.ec.search.product;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.detail.product.ProductDetailDelegate;
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

public class SearchProductAdapter extends MultipleRecyclerAdapter{

//    private final MarketDelegate DELEGATE;
    private final HigoDelegate DELEGATE;
    private int mId  = -1;
    private String mName;

//    private final DataConverter CONVERTER;

    private SearchProductDataConverter  mDataConverter = new SearchProductDataConverter();
    Integer[] productId;


    @Override
    protected MultipleViewHolder createBaseViewHolder(View view) {
        return MultipleViewHolder.create(view);
    }

    public SearchProductAdapter(List<MultipleItemEntity> data, HigoDelegate delegate) {
        super(data);
        DELEGATE = delegate;
        addItemType(ItemType.MARKET, R.layout.item_product);
        addItemType(ItemType.PRODUCT_NO,R.layout.item_no_product);
//       productId = mDataConverter.mProductId.toArray(new Integer[mDataConverter.mProductId.size()]);
    }


    @Override
    protected void convert(final MultipleViewHolder holder, MultipleItemEntity entity) {
        super.convert(holder, entity);

        switch (holder.getItemViewType()) {
                case ItemType.MARKET:
                final String productImg = entity.getField(MultipleFields.IMAGE_URL);
                final String productName = entity.getField(MultipleFields.TITLE);
                mName = productName;
                final Double productPrice = entity.getField(MultipleFields.PRICE);
//                final int id = entity.getField(MultipleFields.ID);
                mId = entity.getField(MultipleFields.ID);

                final String categoryName = entity.getField(MultipleFields.CATEGORY);
                final String username = entity.getField(MultipleFields.USER_NAME);
//                final int quantity = entity.getField(MultipleFields.NUMBER);
                final String typename = entity.getField(MultipleFields.PRODUCT_TYPE);
                final String createTime = entity.getField(MultipleFields.CREATE_TIME);
                final String countryImg = entity.getField(MultipleFields.COUNTRY_IMG);
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

//                GlideApp.with(DELEGATE)
//                        .load(productImg)
//                        .into(mImg);

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
                        .load("http://img.higo.party/"+countryImg)
                        .into(mCountryImg);
//                Glide.with(mContext)
//                        .load(countryImg)
//                        .into(mCountryImg);
                mCountry.setText(countryName);

                break;
            case ItemType.PRODUCT_NO:
                break;
            default:
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                final int productId = mDataConverter.mProductId.get(position);
                final ProductDetailDelegate delegate  = ProductDetailDelegate.create(productId);
                DELEGATE.getSupportDelegate().start(delegate);
            }
        });

    }
}
