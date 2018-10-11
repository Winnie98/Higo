package com.yhl.higo.ec.main.index;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bumptech.glide.Glide;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.detail.product.ProductDetailDelegate;
import com.yhl.higo.ec.detail.promo.PromoDetailDelegate;
import com.yhl.higo.ec.main.market.MarketDataConverter;
import com.yhl.higo.ui.banner.HolderCreator;
import com.yhl.higo.ui.image.GlideApp;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.ui.recycler.MultipleRecyclerAdapter;
import com.yhl.higo.ui.recycler.MultipleViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/26/026.
 */

public class IndexAdapter extends MultipleRecyclerAdapter{

    private final HigoDelegate DELEGATE;
    private int mId  = -1;
    private String mName;

//    private final DataConverter CONVERTER;

    private IndexDataConverter  mDataConverter = new IndexDataConverter();
    Integer[] promoId;

    @Override
    protected MultipleViewHolder createBaseViewHolder(View view) {
        return MultipleViewHolder.create(view);
    }

    public IndexAdapter(List<MultipleItemEntity> data, HigoDelegate delegate) {
        super(data);
        DELEGATE = delegate;
        addItemType(ItemType.INDEX_ITEM, R.layout.item_index);
        addItemType(ItemType.PROMO_PUBLISH_NO, R.layout.item_no_publish);
        addItemType(ItemType.BANNER, R.layout.item_multiple_banner);
//       productId = mDataConverter.mProductId.toArray(new Integer[mDataConverter.mProductId.size()]);
    }

    @Override
    protected void convert(final MultipleViewHolder holder, MultipleItemEntity entity) {
        super.convert(holder, entity);

        final String mainImage;
        final String categoryName;
        final String publisherName;
        final String promoName;
        final Double price;
        final String createTime;

        final String countryImg;
        final String countryName;

        final int statusId;
        final String statusName;
        switch (holder.getItemViewType()) {
            case ItemType.PROMO_PUBLISH_NO:

                break;
            case ItemType.BANNER:
                final List<String> images  = entity.getField(MultipleFields.BANNERS);
                final ConvenientBanner<String> mIndexBanner = holder.getView(R.id.banner_recycler_item);
                mIndexBanner
                        .setPages(new HolderCreator(), images)
                        .setPageIndicator(new int[]{R.drawable.dot_normal, R.drawable.dot_focus})
                        .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                        .setPageTransformer(new DefaultTransformer())
                        .startTurning(3000)
                        .setCanLoop(true);
                break;
            case ItemType.INDEX_ITEM:
                //先取出所有值
                mainImage = entity.getField(MultipleFields.IMAGE_URL);
                categoryName = entity.getField(MultipleFields.CATEGORY);
                publisherName = entity.getField(MultipleFields.PUBLISHER);
                promoName = entity.getField(MultipleFields.PROMOTION);
                price = entity.getField(MultipleFields.PRICE);

                statusId = entity.getField(MultipleFields.PROMO_STATUS_ID);
                statusName = entity.getField(MultipleFields.PROMO_STATUS);
                createTime = entity.getField(MultipleFields.CREATE_TIME);
                countryImg = entity.getField(MultipleFields.COUNTRY_IMG);
                countryName = entity.getField(MultipleFields.COUNTRY_NAME);
//                    statusName = entity.getField(MultipleFields.STATUS);

                //取出所有控件
                final ImageView ImageText = holder.getView(R.id.image_item_promo);
//                    final TextView categoryText = holder.getView(R.id.tv_item_promo_category);
                final TextView publisherText = holder.getView(R.id.tv_promo_publisher);
                final TextView promoText = holder.getView(R.id.tv_item_promo);
                final TextView priceText = holder.getView(R.id.tv_item_promo_price);

                final TextView publishTime = holder.getView(R.id.txt_item_promo_time);
                final Button status = holder.getView(R.id.btn_item_promo_status);
                final Button category = holder.getView(R.id.btn_item_promo_category);

                final ImageView mCountryImg = holder.getView(R.id.img_promo_country);
                final TextView mCountry = holder.getView(R.id.txt_promo_country);
//                    final AppCompatTextView statusText = holder.getView(R.id.tv_promo_status);

                //赋值
//                    categoryText.setText(categoryName);
                publisherText.setText(publisherName);
                promoText.setText(promoName);
                priceText.setText(String.valueOf(price));
                category.setText(categoryName);
                status.setText(statusName);
                publishTime.setText(createTime);
//                    statusText.setText(statusName);
                Glide.with(mContext)
                        .load("http://img.higo.party/"+mainImage)
                        .into(ImageText);

                GlideApp.with(mContext)
                        .load("http://img.higo.party/"+countryImg)
                        .into(mCountryImg);
                mCountry.setText(countryName);

                break;
            default:
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();

                final int promoId = mDataConverter.mPromoId.get(position);
                final PromoDetailDelegate  delegate  = PromoDetailDelegate.create(promoId);
                DELEGATE.getParentDelegate().getSupportDelegate().start(delegate);
            }
        });

    }
}
