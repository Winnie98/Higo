package com.yhl.higo.ec.main.sort.content;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yhl.higo.ec.R;
import com.yhl.higo.ec.detail.promo.PromoDetailDelegate;
import com.yhl.higo.ec.main.EcBottomDelegate;
import com.yhl.higo.ec.main.sort.SortDelegate;
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

public class ContentAdapter extends MultipleRecyclerAdapter{

    private final ContentDelegate DELEGATE;
    private ContentDataConverter  mDataConverter = new ContentDataConverter();

    protected ContentAdapter(List<MultipleItemEntity> data, ContentDelegate delegate) {
        super(data);
        DELEGATE = delegate;
        addItemType(ItemType.SORT_CONTENT, R.layout.item_promo_content);
        addItemType(ItemType.PROMO_PUBLISH_NO, R.layout.item_no_publish_sort);
    }

    @Override
    protected void convert(final MultipleViewHolder holder, MultipleItemEntity entity) {
        super.convert(holder, entity);
        switch (holder.getItemViewType()) {
            case ItemType.PROMO_PUBLISH_NO:

                break;
            case ItemType.SORT_CONTENT:
                final String name = entity.getField(MultipleFields.NAME);
                final String imgUrl = entity.getField(MultipleFields.IMAGE_URL);
                final Double price = entity.getField(MultipleFields.PRICE);
                final int id = entity.getField(MultipleFields.ID);

                final String countryImg = entity.getField(MultipleFields.COUNTRY_IMG);
                final String countryName = entity.getField(MultipleFields.COUNTRY_NAME);
                final String publisher = entity.getField(MultipleFields.PUBLISHER);
                final String createTime = entity.getField(MultipleFields.CREATE_TIME);

                final AppCompatImageView img = holder.getView(R.id.img_content_promo);
                final AppCompatTextView promoText = holder.getView(R.id.tv_content_promo_name);
                final AppCompatTextView priceText = holder.getView(R.id.tv_content_promo_price);
                final ImageView mCountryImg = holder.getView(R.id.img_content_promo_country);
                final TextView mCountry = holder.getView(R.id.txt_content_promo_country);
                final TextView  mPublisher = holder.getView(R.id.txt_promo_publisher);
                final TextView mPublishTime = holder.getView(R.id.txt_promo_publishTime);

                    GlideApp.with(DELEGATE)
                            .load(imgUrl)
                            .into(img);
                    promoText.setText(name);
                    priceText.setText(String.valueOf(price));

                    GlideApp.with(mContext)
                            .load("http://img.higo.party/"+countryImg)
                            .into(mCountryImg);
                    mCountry.setText(countryName);
                    mPublisher.setText(publisher);
                    mPublishTime.setText(createTime);

                break;
            default:
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();

                final int promoId = mDataConverter.mSortPromoId.get(position);
                final SortDelegate sortDelegate  = DELEGATE.getParentDelegate();
                final EcBottomDelegate ecBottomDelegate = sortDelegate.getParentDelegate();
//                mRecyclerView.addOnItemTouchListener(ContentItemClickListener.create(ecBottomDelegate));

                final PromoDetailDelegate  delegate  = PromoDetailDelegate.create(promoId);
                ecBottomDelegate.getSupportDelegate().start(delegate);
            }
        });

    }

}
