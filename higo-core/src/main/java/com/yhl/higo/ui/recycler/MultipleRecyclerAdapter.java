package com.yhl.higo.ui.recycler;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yhl.higo.R;
import com.yhl.higo.ui.banner.BannerCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/15/015.
 */

public class MultipleRecyclerAdapter extends
        BaseMultiItemQuickAdapter<MultipleItemEntity,MultipleViewHolder>
        implements
//        BaseQuickAdapter.SpanSizeLookup ,
        OnItemClickListener{

    //确保初始化一次Banner,防止重复Item加载
    private boolean mIsInitBanner = false;
    //设置图片加载策略
    private static final RequestOptions RECYCLER_OPTIONS =
            new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate();

    protected MultipleRecyclerAdapter(List<MultipleItemEntity> data) {
        super(data);
        init();
    }

    public static MultipleRecyclerAdapter create(List<MultipleItemEntity> data){
        return new MultipleRecyclerAdapter(data);
    }

    public static MultipleRecyclerAdapter create(DataConverter converter){
        return new MultipleRecyclerAdapter(converter.convert());
    }

    private void init(){
        //设置不同的item布局
        addItemType(ItemType.TEXT, R.layout.item_multiple_text);
        addItemType(ItemType.IMAGE,R.layout.item_multiple_image);
        addItemType(ItemType.TEXT_IMAGE,R.layout.item_multiple_image_text);
        addItemType(ItemType.BANNER,R.layout.item_multiple_banner);

        addItemType(ItemType.INDEX_ITEM,R.layout.item_index);
        addItemType(ItemType.PROMO_PUBLISH_NO,R.layout.item_no_publish);

        //设置宽度监听
//        setSpanSizeLookup(this);
        openLoadAnimation();
        //多次执行动画
        isFirstOnly(false);
    }

    @Override
    protected MultipleViewHolder createBaseViewHolder(View view) {
        return MultipleViewHolder.create(view);
    }

    //进行数据的转换，即什么样的数据转换成什么样的视图
    @Override
    protected void convert(MultipleViewHolder holder, MultipleItemEntity entity) {
        final String text;
        final String imageUrl;
        final ArrayList<String> bannerImages;

        final String mainImage;
        final String categoryName;
        final String publisherName;
        final String promoName;
        final Double price;
        final String createTime;

        final int statusId;
        final String statusName;

        switch (holder.getItemViewType()){
            case ItemType.TEXT:
                text = entity.getField(MultipleFields.TEXT);
                holder.setText(R.id.text_single,text);
                break;
            case ItemType.IMAGE:
                imageUrl = entity.getField(MultipleFields.IMAGE_URL);
//                Glide.with(mContext)
//                        .load(imageUrl)
////                        .diskCacheStrategy(DiskCacheStrategy.ALL)
////                        .dontAnimate()
////                        .centerCrop()
//                        .apply(RECYCLER_OPTIONS)
//                        .into((ImageView) holder.getView(R.id.img_single));
                break;
            case ItemType.TEXT_IMAGE:
                text = entity.getField(MultipleFields.TEXT);
                imageUrl = entity.getField(MultipleFields.IMAGE_URL);
                Glide.with(mContext)
                        .load(imageUrl)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .dontAnimate()
//                        .centerCrop()
                        .apply(RECYCLER_OPTIONS)
                        .into((ImageView) holder.getView(R.id.img_multiple));
                holder.setText(R.id.tv_multiple,text);
                break;
            case ItemType.BANNER:
                bannerImages = entity.getField(MultipleFields.BANNERS);
                final ConvenientBanner<String> convenientBanner = holder.getView(R.id.banner_recycler_item);
                BannerCreator.setDefault(convenientBanner,bannerImages,this);
                mIsInitBanner = true;
                break;
            case ItemType.PROMO_PUBLISH_NO:

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

                break;
            default:
                break;
        }
    }

//    @Override
//    public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
////        return getData().get(position).getField(MultipleFields.SPAN_SIZE);
//        return 4;
//    }

    @Override
    public void onItemClick(int position) {

    }
}
