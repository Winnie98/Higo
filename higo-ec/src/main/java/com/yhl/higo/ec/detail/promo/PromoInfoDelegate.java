package com.yhl.higo.ec.detail.promo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.main.personal.profile.OthersUserInfoDelegate;
import com.yhl.higo.ui.image.GlideApp;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2018/6/8/008.
 */

public class PromoInfoDelegate extends HigoDelegate {
    private int mPublisherId;
    private int mPublisherTypeId;

    @BindView(R2.id.tv_promo_info_title)
    TextView mPromoInfoTitle = null;
    @BindView(R2.id.tv_promo_info_category)
    TextView mPromoInfoCategory = null;
    @BindView(R2.id.tv_promo_info_price)
    TextView mPromoInfoPrice = null;
    @BindView(R2.id.img_promo_info_publisher)
    CircleImageView mPublisherAvatar = null;
    @BindView(R2.id.txt_promo_info_publisher)
    TextView mPublisherName = null;
    @BindView(R2.id.txt_promo_info_publishTime)
    TextView mPublishTime = null;
    @BindView(R2.id.img_promo_info_country)
    ImageView mCountryImg = null;
    @BindView(R2.id.txt_promo_info_country)
    TextView mCountryName = null;

    private JSONObject mData = null;

    @OnClick(R2.id.img_promo_info_publisher)
    void onClickUserInfo(){
        if (mPublisherTypeId != 1){
            final PromoDetailDelegate delegate  = getParentDelegate();
            delegate.getSupportDelegate().start(OthersUserInfoDelegate.create(mPublisherId));
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        final String promoInfo = args.getString("promoInfo");
        mData = JSON.parseObject(promoInfo);
    }

    public static PromoInfoDelegate create(String promoInfo) {
        final Bundle args = new Bundle();
        args.putString("promoInfo", promoInfo);
        final PromoInfoDelegate delegate = new PromoInfoDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_promo_info;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        final String name = mData.getString("name");
        final String category = mData.getString("categoryName");
        final double price = mData.getDouble("price");
        final String avatar = mData.getString("publisherAvatar");
        final String publishTime = mData.getString("createTime");
        final String countryFlag = mData.getString("countryFlag");
        final String publisherName = mData.getString("publisherName");
        final int publisherTypeId = mData.getInteger("publisherTypeId");
        mPublisherTypeId = publisherTypeId;
        final String countryName = mData.getString("countryName");
        final String imageHost = mData.getString("imageHost");
        mPublisherId = mData.getInteger("publisherId");

        mPromoInfoTitle.setText(name);
        mPromoInfoCategory.setText(category);
        mPromoInfoPrice.setText(String.valueOf(price));
        mPublishTime.setText(publishTime);
        GlideApp.with(getContext())
                .load(imageHost+countryFlag)
                .into(mCountryImg);
        mCountryName.setText(countryName);
        mPublisherName.setText(publisherName);
        GlideApp.with(getContext())
                .load(imageHost+avatar)
                .into(mPublisherAvatar);

    }
}
