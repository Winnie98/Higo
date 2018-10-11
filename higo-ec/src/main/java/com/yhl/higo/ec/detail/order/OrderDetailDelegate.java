package com.yhl.higo.ec.detail.order;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yhl.higo.delegates.HigoDelegate;

import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.detail.feedback.FeedbackDetailDelegate;
import com.yhl.higo.ec.detail.tracking.OrderTrackingDetailDelegate;
import com.yhl.higo.ec.main.personal.order.OrderConfirmDelegate;
import com.yhl.higo.ec.sign.SignInCodeDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;

import com.yhl.higo.ui.image.GlideApp;
import com.yhl.higo.util.log.HigoLogger;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/18/018.
 */

public class OrderDetailDelegate extends HigoDelegate {

    private long orderNo;
    private int currentUserId;
    AlertDialog mDialog;
    private int mScoreCount = 5;
    private double totalPrice;
    private EditText mFeedbackDetail;
    private EditText mTrackingNo;
    private TextView scoreText;


    @BindView(R2.id.txt_order_detail_name)
    TextView mReceiver = null;
    @BindView(R2.id.txt_order_detail_phone)
    TextView mPhone = null;
    @BindView(R2.id.txt_order_detail_address)
    TextView mAddress = null;
    @BindView(R2.id.txt_order_detail_seller)
    TextView mSeller = null;
    @BindView(R2.id.img_order_detail)
    ImageView mImg = null;
    @BindView(R2.id.txt_order_detail_title)
    TextView mTitle = null;
    @BindView(R2.id.txt_order_detail_num)
    TextView mQuantity = null;
    @BindView(R2.id.txt_order_detail_price)
    TextView mProductPrice = null;
    @BindView(R2.id.txt_order_detail_total_price)
    TextView mTotalPrice = null;
    @BindView(R2.id.txt_order_detail_product_price)
    TextView mTotalProductPrice = null;
    @BindView(R2.id.txt_order_detail_tax_price)
    TextView mTotalTaxPrice = null;

    @BindView(R2.id.txt_order_detail_orderNo)
    TextView mOrderNo = null;
    @BindView(R2.id.txt_order_detail_createTime)
    TextView mCreateTime = null;
    @BindView(R2.id.txt_order_detail_updateTime)
    TextView mUpdateTime = null;
    @BindView(R2.id.txt_order_detail_payTime)
    TextView mPayTime = null;
    @BindView(R2.id.txt_order_detail_sendTime)
    TextView mSendTime = null;
    @BindView(R2.id.txt_order_detail_receiveTime)
    TextView mReceiveTime = null;
    @BindView(R2.id.txt_order_detail_completeTime)
    TextView mCompleteTime = null;
    @BindView(R2.id.txt_order_detail_status)
    TextView mOrderStatus = null;

    @BindView(R2.id.cancel_order)
    Button cancel_order = null;
    @BindView(R2.id.btn_order_pay_buyer)
    Button btn_order_pay_buyer = null;
    @BindView(R2.id.btn_order_send_seller)
    Button btn_order_send_seller = null;
    @BindView(R2.id.btn_order_confirm_buyer)
    Button btn_order_confirm_buyer = null;

    @BindView(R2.id.frame_item)
    FrameLayout frame_item = null;
    @BindView(R2.id.frame_order_pay_buyer)
    FrameLayout frame_order_pay_buyer = null;
    @BindView(R2.id.frame_order_send_seller)
    FrameLayout frame_order_send_seller = null;
    @BindView(R2.id.frame_order_confirm_buyer)
    FrameLayout frame_order_confirm_buyer = null;
    @BindView(R2.id.frame_order_feedback)
    FrameLayout frame_order_feedback_buyer  = null;

    @BindView(R2.id.txt_order_detail_trackingNo)
    TextView mOrderTrackingNo = null;

    @OnClick(R2.id.ll_toolbar)
    void onClickBack(){
        getSupportDelegate().pop();
    }

    //查看物流信息
    @OnClick(R2.id.rl_order_detail_tracking)
    void onClickTrackingDetail(){
        final OrderTrackingDetailDelegate delegate  = OrderTrackingDetailDelegate.create(mOrderTrackingNo.getText().toString());
        getSupportDelegate().start(delegate);
    }

    //反馈信息
    public void beginSendFeedback(){
        mDialog = new AlertDialog.Builder(getContext()).create();
        mDialog.show();
        final Window window = mDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_send_feedback);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置属性
            final WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(params);

            //添加事件
            mFeedbackDetail = window.findViewById(R.id.edt_send_feedback);

            //添加事件
            window.findViewById(R.id.btn_dialog_send_feedback).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFeedbackDetail.getText().toString().isEmpty()){
                        Toast.makeText(getContext(),"反馈信息不能为空！",Toast.LENGTH_SHORT).show();
                    }else {
                        RestClient.builder()
                                .url("user/feedback/send_feedback.do")
                                .params("detail",mFeedbackDetail.getText().toString())
                                .success(new ISuccess() {
                                    @Override
                                    public void onSuccess(String response) {
                                        HigoLogger.d("SEND_FEEDBACK",response);
                                        final int status = JSON.parseObject(response).getInteger("status");
                                        final String msg = JSON.parseObject(response).getString("msg");
                                        switch (status){
                                            case 0:
                                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                                break;
                                            case 1:
                                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                                break;
                                            default:
                                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    }
                                })
                                .build()
                                .post();
                        mDialog.cancel();
                    }
                }
            });
        }
    }

    //订单发货
    public void beginSendOrder(){
        mDialog = new AlertDialog.Builder(getContext()).create();
        mDialog.show();
        final Window window = mDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_send_order);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置属性
            final WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(params);

            //添加事件
            mTrackingNo = window.findViewById(R.id.edt_send_order);

            //添加事件
            window.findViewById(R.id.btn_dialog_send_order).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RestClient.builder()
                            .url("user/order/dispatch.do")
                            .params("orderNo",orderNo)
                            .params("trackingNo",mTrackingNo.getText().toString())
                            .success(new ISuccess() {
                                @Override
                                public void onSuccess(String response) {
                                    HigoLogger.d("ORDER_SEND",response);
                                    final String data = JSON.parseObject(response).getString("data");
                                    Toast.makeText(getContext(),data,Toast.LENGTH_SHORT).show();
                                }
                            })
                            .build()
                            .post();
                    mDialog.cancel();
                }
            });
        }
    }

    //确认收货
    public void beginConfirmOrder(){
        mDialog = new AlertDialog.Builder(getContext()).create();
        mDialog.show();
        final Window window = mDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_score);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置属性
            final WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(params);

            //添加事件
            RatingBar mRtBar = window.findViewById(R.id.rb_bar);
            scoreText = window.findViewById(R.id.txt_score_content);

            //添加事件
            window.findViewById(R.id.order_score_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.cancel();
                }
            });
            mRtBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    mScoreCount = (int) rating;
                    switch (mScoreCount){
                        case 1:
                            scoreText.setText("非常差");
                            break;
                        case 2:
                            scoreText.setText("差");
                            break;
                        case 3:
                            scoreText.setText("一般");
                            break;
                        case 4:
                            scoreText.setText("好");
                            break;
                        case 5:
                            scoreText.setText("非常好");
                            break;
                        default:
                            break;

                    }
                }
            });
            window.findViewById(R.id.btn_score).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RestClient.builder()
                            .url("user/order/receipt.do")
                            .params("mOrderNo", orderNo)
                            .params("score",mScoreCount)
                            .success(new ISuccess() {
                                @Override
                                public void onSuccess(String response) {
                                    HigoLogger.d("RECEIVE_PRODUCT",response);
                                    final int status = JSON.parseObject(response).getInteger("status");
                                    switch (status){
                                        case 0:
                                            final String data = JSON.parseObject(response).getString("data");
                                            Toast.makeText(getContext(),data,Toast.LENGTH_SHORT).show();
                                            mDialog.dismiss();// 对话框消失
                                            break;
                                        case 3:
                                            final String need_login = JSON.parseObject(response).getString("data");
                                            Toast.makeText(getContext(),need_login,Toast.LENGTH_SHORT).show();
                                            break;
                                        case 4:
                                            Toast.makeText(getContext(),"非法请求",Toast.LENGTH_SHORT).show();
                                            break;
                                        default:
                                            final String msg = JSON.parseObject(response).getString("data");
                                            Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            })
                            .build()
                            .post();
                    mDialog.cancel();
                }
            });
        }
    }

    //反馈信息
    @OnClick(R2.id.btn_order_detail_feedback)
    void onClickSendFeedback(){
        RestClient.builder()
                .url("user/feedback/check_feedback.do")
                .params("orderNo",orderNo)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("SEND_FEEDBACK",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        final String msg = JSON.parseObject(response).getString("msg");
                        switch (status){
                            case 0:
//                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                final int feedbackId = JSON.parseObject(response).getInteger("data");
                                final FeedbackDetailDelegate delegate  = FeedbackDetailDelegate.create(feedbackId);
                                getSupportDelegate().start(delegate);
                                break;
                            case 1:
                                beginSendFeedback();
//                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
//                                            final int feedbackId = JSON.parseObject(response).getInteger("msg");
//                                            final FeedbackDetailDelegate delegate  = FeedbackDetailDelegate.create(feedbackId);
//                                            getSupportDelegate().start(delegate);
                                break;
                            default:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })
                .build()
                .post();

    }
    //取消订单
    @OnClick(R2.id.cancel_order)
    void cancelOrder(){
        RestClient.builder()
                .url("user/order/cancel_order.do")
                .params("orderNo",orderNo)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("CANCEL_ORDER",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        final String msg = JSON.parseObject(response).getString("msg");
                        switch (status){
                            case 0:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
//                                remove(v.getId());
//                                notifyDataSetChanged();
                                getSupportDelegate().pop();
                                break;
                            case 3:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                getSupportDelegate().start(new SignInCodeDelegate());
                                break;
                            default:
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })
                .build()
                .post();
    }
    //立即付款
    @OnClick(R2.id.btn_order_pay_buyer)
    void payOrder(){
        final OrderConfirmDelegate delegate  = OrderConfirmDelegate.create(orderNo,Double.parseDouble(mTotalPrice.getText().toString()));
        getSupportDelegate().start(delegate);
//        remove(v.getId());
    }
    //立即发货
    @OnClick(R2.id.btn_order_send_seller)
    void sendOrder(){
        beginSendOrder();
    }
    //确认收货
    @OnClick(R2.id.btn_order_confirm_buyer)
    void confirmOrder(){
        beginConfirmOrder();
    }



    public static OrderDetailDelegate create(long orderNo, int currentUserId){
        final Bundle args = new Bundle();
        args.putLong("orderNo", orderNo);
        args.putInt("currentUserId",currentUserId);
        final OrderDetailDelegate delegate = new OrderDetailDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            orderNo = args.getLong("orderNo");
            currentUserId = args.getInt("currentUserId");
        }
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_order_detail;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initOrderDetailData();
    }

    private void initOrderDetailData() {
        frame_item.removeView(frame_order_pay_buyer);
        frame_item.removeView(frame_order_send_seller);
        frame_item.removeView(frame_order_confirm_buyer);
        frame_item.removeView(frame_order_feedback_buyer);

        RestClient.builder()
                .url("user/order/get_order_detail.do")
                .params("orderNo", orderNo)
                .loader(getContext())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("ORDER_NO",orderNo);
                        HigoLogger.d("ORDER_INFO_DETAILS",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                final JSONObject data = JSON.parseObject(response).getJSONObject("data");
                                final long orderNo = data.getLong("orderNo");
                                final String imageHost = data.getString("imageHost");
                                final String productImage = data.getString("productImage");
                                final int productId = data.getInteger("productId");
                                final String productName = data.getString("productName");
                                final int sellerUserId = data.getInteger("sellerUserId");
                                final String sellerUsername = data.getString("sellerUsername");
                                final int quantity = data.getInteger("productQuantity");
                                final Double productPrice = data.getDouble("productPrice");

                                final Double productTax = data.getDouble("productTax");
                                final Double totalProductPrice = productPrice*quantity;
                                final Double totalProductTax = productTax*quantity;

                                final Double totalAmount = data.getDouble("totalAmount");
                                final int statusId = data.getInteger("statusId");
                                final String trackingNo = data.getString("trackingNo");

                                final String createTime = data.getString("createTime");
                                final String updateTime = data.getString("updateTime");
                                final String payTime = data.getString("payTime");
                                final String sendTime = data.getString("dispatchTime");
                                final String receiptTime = data.getString("receiptTime");
                                final String closeTime = data.getString("closeTime");

                                final String address = data.getString("address");
                                if (address!= null){
                                    String[] addressArray = address.split(",");

                                    mReceiver.setText(addressArray[0]);
                                    mPhone.setText(addressArray[1]);
                                    mAddress.setText(addressArray[2]);
                                }

                                if (currentUserId == sellerUserId){
                                    switch (statusId){
                                        case 0:
                                            //已取消
                                            mOrderStatus.setText("交易已取消");
                                            break;
                                        case 1:
                                            //未付款
                                            mOrderStatus.setText("买家未付款");
                                            cancel_order.setVisibility(View.VISIBLE);
                                            break;
                                        case 2:
                                            //已付款
                                            mOrderStatus.setText("待发货");
                                            frame_item.addView(frame_order_send_seller);
                                            break;
                                        case 3:
                                            //已发货
                                            mOrderStatus.setText("已发货");
                                            break;
                                        case 4:
                                            //已完成
                                            mOrderStatus.setText("交易成功");
                                            frame_item.addView(frame_order_feedback_buyer);
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
                                            cancel_order.setVisibility(View.VISIBLE);
                                            frame_item.addView(frame_order_pay_buyer);
                                            break;
                                        case 2:
                                            //已付款
                                            mOrderStatus.setText("卖家未发货");
                                            break;
                                        case 3:
                                            //已发货
                                            mOrderStatus.setText("卖家已发货");
                                            frame_item.addView(frame_order_confirm_buyer);
                                            break;
                                        case 4:
                                            //已完成
                                            mOrderStatus.setText("交易成功");
                                            frame_item.addView(frame_order_feedback_buyer);
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
                                GlideApp.with(mImg)
                                        .load(imageHost+productImage)
                                        .into(mImg);
                                mTitle.setText(productName);
                                mQuantity.setText(String.valueOf(quantity));
                                mProductPrice.setText(String.valueOf(productPrice));
                                mOrderTrackingNo.setText(trackingNo);

                                mTotalProductPrice.setText(String.valueOf(totalProductPrice));
                                mTotalPrice.setText(String.valueOf(totalAmount));
                                mTotalTaxPrice.setText(String.valueOf(totalProductTax));
                                mOrderNo.setText(String.valueOf(orderNo));
                                mCreateTime.setText(String.valueOf(createTime));
                                mUpdateTime.setText(String.valueOf(updateTime));
                                mPayTime.setText(String.valueOf(payTime));
                                mSendTime.setText(String.valueOf(sendTime));
                                mReceiveTime.setText(String.valueOf(receiptTime));
                                mCompleteTime.setText(String.valueOf(closeTime));


                                break;
                            case 1:
                                final String msg = JSON.parseObject(response).getString("msg");
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                getSupportDelegate().pop();
                                break;
                            default:
                                break;
                        }

                    }
                })
                .build()
                .get();
    }

}
