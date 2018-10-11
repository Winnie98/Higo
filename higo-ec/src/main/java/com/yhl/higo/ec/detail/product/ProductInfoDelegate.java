package com.yhl.higo.ec.detail.product;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.detail.promo.PromoDetailDelegate;
import com.yhl.higo.ec.main.personal.profile.OthersUserInfoDelegate;
import com.yhl.higo.ui.image.GlideApp;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2018/6/8/008.
 */

public class ProductInfoDelegate extends HigoDelegate {
    private int mUserId;

    @BindView(R2.id.tv_product_info_title)
    TextView mProductInfoTitle = null;
    @BindView(R2.id.tv_product_info_sub_title)
    TextView mProductInfoSubTitle = null;
    @BindView(R2.id.tv_product_info_price)
    TextView mProductInfoPrice = null;
    @BindView(R2.id.tv_product_info_quantity)
    TextView mProductQuantity = null;
    @BindView(R2.id.btn_product_category)
    Button mProductCategory = null;
    @BindView(R2.id.btn_product_type)
    Button mProductType = null;
    @BindView(R2.id.tv_product_info_tax)
    TextView mProductInfoTax = null;
    @BindView(R2.id.img_product_info_publisher)
    CircleImageView mAvatar = null;
    @BindView(R2.id.txt_product_info_publisher)
    TextView mPublisher = null;
    @BindView(R2.id.txt_product_info_publishTime)
    TextView mPublishTime = null;
    @BindView(R2.id.img_product_info_country)
    ImageView mCountryImg = null;
    @BindView(R2.id.txt_product_info_country)
    TextView mCountryName = null;

    private JSONObject mData = null;

    @OnClick(R2.id.img_product_info_publisher)
    void onClickUserInfo(){
        final ProductDetailDelegate delegate  = getParentDelegate();
        delegate.getSupportDelegate().start(OthersUserInfoDelegate.create(mUserId));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        final String productInfo = args.getString("productInfo");
        mData = JSON.parseObject(productInfo);
    }

    public static ProductInfoDelegate create(String productInfo) {
        final Bundle args = new Bundle();
        args.putString("productInfo", productInfo);
        final ProductInfoDelegate delegate = new ProductInfoDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_product_info;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        final String name = mData.getString("name");
        final String subtitle = mData.getString("subtitle");
        final String category = mData.getString("categoryName");
        final double price = mData.getDouble("price");
        final String typeName = mData.getString("typeName");
        final int quantity = mData.getInteger("quantity");
        final double tax = mData.getDouble("tax");
        final String countryFlag = mData.getString("countryFlag");
        final String publisherName = mData.getString("username");
        final String countryName = mData.getString("countryName");
        final String avatar = mData.getString("avatar");
        final String imageHost = mData.getString("imageHost");
        mUserId = mData.getInteger("userId");
        final String createTime = mData.getString("createTime");

        mProductInfoTitle.setText(name);
        mProductInfoSubTitle.setText(subtitle);
        mProductCategory.setText(category);
        mProductInfoPrice.setText(String.valueOf(price));
        mProductType.setText(typeName);
        mProductQuantity.setText(String.valueOf(quantity));
        mProductInfoTax.setText(String.valueOf(tax));

        mPublishTime.setText(createTime);
        GlideApp.with(getContext())
                .load(imageHost+countryFlag)
                .into(mCountryImg);
        mCountryName.setText(countryName);
        mPublisher.setText(publisherName);
        GlideApp.with(getContext())
                .load(imageHost+avatar)
                .into(mAvatar);

    }
}
