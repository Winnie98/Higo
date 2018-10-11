package com.yhl.higo.ec.main.personal.order;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.detail.order.OrderDetailDelegate;
import com.yhl.higo.ec.identify.IdentifyDelegate;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.ec.pay.IAlPayResultListener;
import com.yhl.higo.ec.sign.SignInCodeDelegate;
import com.yhl.higo.ec.sign.SignInDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.recycler.ItemType;
import com.yhl.higo.ui.recycler.MultipleFields;
import com.yhl.higo.ui.recycler.MultipleItemEntity;
import com.yhl.higo.ui.recycler.MultipleRecyclerAdapter;
import com.yhl.higo.ui.recycler.MultipleViewHolder;
import com.yhl.higo.ui.widget.StarLayout;
import com.yhl.higo.util.log.HigoLogger;

import java.util.List;

import static com.blankj.utilcode.util.Utils.getContext;

/**
 * Created by Administrator on 2018/5/20/020.
 */

public class OrderListAdapter extends MultipleRecyclerAdapter
        implements
        IAlPayResultListener {

    AlertDialog mDialog;
    private final HigoDelegate DELEGATE;
    private final int CURRENT_ID;
    private IAlPayResultListener iAlPayResultListener;
    private int mScoreCount;

//    private final OrderListDelegate DELEGATE;



    private static final RequestOptions OPTIONS = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .dontAnimate();

    protected OrderListAdapter(List<MultipleItemEntity> data, HigoDelegate delegate, int currentId, int status) {
        super(data);
        this.DELEGATE = delegate;
        this.CURRENT_ID = currentId;
//        addItemType(OrderListItemType.ITEM_ORDER_LIST, R.layout.item_order_list);
        if (status == 0){
            addItemType(ItemType.ITEM_ORDER_LIST, R.layout.item_order);
        }else if (status == 1){
            addItemType(ItemType.ITEM_ORDER_NO, R.layout.item_no_order);
        }
    }

    
    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(MultipleViewHolder holder, MultipleItemEntity entity) {
        super.convert(holder, entity);
        switch (holder.getItemViewType()){
            case ItemType.ITEM_ORDER_LIST:
                //绑定视图
                final ImageView imageView = holder.getView(R.id.img_order);
                final TextView mTitle = holder.getView(R.id.txt_order_title);
                final TextView mPrice = holder.getView(R.id.txt_order_price);
                final TextView mQuantity = holder.getView(R.id.txt_item_order_num);

                final TextView mTotalPrice = holder.getView(R.id.txt_order_total_price);
                final TextView mSeller = holder.getView(R.id.txt_order_item_seller);
                final TextView mOrderStatus = holder.getView(R.id.txt_order_item_order_status);

                final LinearLayout mLayout = holder.getView(R.id.ll_order);

                final String title = entity.getField(MultipleFields.TITLE);
                final Double price = entity.getField(MultipleFields.PRICE);
                final int quantity = entity.getField(MultipleFields.NUMBER);
                final double totalPrice = entity.getField(MultipleFields.TOTAL_PRICE);
                final String imgUrl = entity.getField(MultipleFields.IMAGE_URL);
                final int statusId = entity.getField(MultipleFields.STATUS_ID);
                final String sellerUsername = entity.getField(MultipleFields.SELLER);
                final int sellerUserId = entity.getField(MultipleFields.SELLER_ID);
                final long orderNo = entity.getField(MultipleFields.ORDER_NO);

                if (CURRENT_ID == sellerUserId){
                    switch (statusId){
                        case 0:
                            //已取消
                            mOrderStatus.setText("交易已取消");
                            break;
                        case 1:
                            //未付款
                            mOrderStatus.setText("买家未付款");
//                            cancel_order.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            //已付款
                            mOrderStatus.setText("待发货");
//                            frame_item.addView(frame_order_send_seller);
                            break;
                        case 3:
                            //已发货
                            mOrderStatus.setText("已发货");
                            break;
                        case 4:
                            //已完成
                            mOrderStatus.setText("交易成功");
                            break;
                        case 5:
                            //已关闭
                            mOrderStatus.setText("已关闭");
                            break;
                        default:
                            break;
                    }
                }else{
                    switch (statusId){
                        case 0:
                            //已取消
                            mOrderStatus.setText("交易已取消");
                            break;
                        case 1:
                            //未付款
                            mOrderStatus.setText("待付款");
//                            cancel_order.setVisibility(View.VISIBLE);
//                            frame_item.addView(frame_order_pay_buyer);
                            break;
                        case 2:
                            //已付款
                            mOrderStatus.setText("卖家未发货");
                            break;
                        case 3:
                            //已发货
                            mOrderStatus.setText("卖家已发货");
//                            frame_item.addView(frame_order_confirm_buyer);
                            break;
                        case 4:
                            //已完成
                            mOrderStatus.setText("交易成功");
                            break;
                        case 5:
                            //已关闭
                            mOrderStatus.setText("已关闭");
                            break;
                        default:
                            break;
                    }
                }

                mSeller.setText(sellerUsername);
                Glide.with(mContext)
                        .load(imgUrl)
                        .apply(OPTIONS)
                        .into(imageView);

                mTitle.setText(title);

                mQuantity.setText(String.valueOf(quantity));
                mPrice.setText(String.valueOf(price));
                mTotalPrice.setText(String.valueOf(totalPrice));

                mLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final OrderDetailDelegate delegate  = OrderDetailDelegate.create(orderNo,CURRENT_ID);
                        DELEGATE.getSupportDelegate().start(delegate);
                    }
                });

                break;
            case ItemType.ITEM_ORDER_NO:

                break;
            default:
                break;
        }

    }


    @Override
    public void onPaySuccess() {

    }

    @Override
    public void onPaying() {

    }

    @Override
    public void onPayFail() {

    }

    @Override
    public void onPayCancel() {

    }

    @Override
    public void onPayConnectError() {

    }

}

