package com.yhl.higo.ec.main.personal.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.identify.IdentifyDelegate;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.ec.main.personal.address.SelectAddressDelegate;
import com.yhl.higo.ec.pay.FastPay;
import com.yhl.higo.ec.pay.IAlPayResultListener;
import com.yhl.higo.ec.sign.SignInCodeDelegate;
import com.yhl.higo.ec.sign.SignInDelegate;
import com.yhl.higo.net.RestClient;
import com.yhl.higo.net.callback.ISuccess;
import com.yhl.higo.ui.image.GlideApp;
import com.yhl.higo.util.log.HigoLogger;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/18/018.
 */

public class OrderConfirmDelegate extends HigoDelegate implements IAlPayResultListener {

    private long orderNo;
    private double orderAmount;
//    private int mAddressId;

    @BindView(R2.id.txt_order_confirm_name)
    TextView mReceiver = null;
    @BindView(R2.id.txt_order_confirm_phone)
    TextView mPhone = null;
    @BindView(R2.id.txt_order_confirm_address)
    TextView mAddress = null;
    @BindView(R2.id.txt_order_confirm_seller)
    TextView mSeller = null;
    @BindView(R2.id.img_order_confirm)
    ImageView mImg = null;
    @BindView(R2.id.txt_order_confirm_title)
    TextView mTitle = null;
    @BindView(R2.id.txt_order_confirm_num)
    TextView mQuantity = null;
    @BindView(R2.id.txt_order_confirm_price)
    TextView mPrice = null;
    @BindView(R2.id.txt_order_confirm_total_price)
    TextView mTotalPrice = null;
    @BindView(R2.id.txt_order_confirm_pay_price)
    TextView mPayPrice = null;

    @BindView(R2.id.txt_order_confirm_product_price)
    TextView mTotalProductPrice = null;
    @BindView(R2.id.txt_order_confirm_tax_price)
    TextView mTotalTaxPrice = null;

    @BindView(R2.id.txt_order_confirm_orderNo)
    TextView mOrderNo = null;
    @BindView(R2.id.txt_order_confirm_createTime)
    TextView mCreateTime = null;

    @BindView(R2.id.frame_order_confirm_layout)
    FrameLayout mConfirmFrameLayout = null;
    @BindView(R2.id.frame_order_confirm_select_address)
    FrameLayout mSelectFrameLayout = null;
    @BindView(R2.id.frame_order_confirm_address)
    FrameLayout mAddressFrameLayout = null;



    @OnClick(R2.id.icon_order_confirm_back)
    void onClickBack(){
        getSupportDelegate().pop();
    }
    //更新订单地址
    @OnClick(R2.id.rl_order_confirm_select_address)
    void onClickSelectAddress(){
        getSupportDelegate().startForResult(new SelectAddressDelegate(),1);
        mConfirmFrameLayout.removeView(mSelectFrameLayout);
        mConfirmFrameLayout.addView(mAddressFrameLayout);
    }
    @OnClick(R2.id.rl_order_confirm_update_address)
    void onClickUpdateAddress(){
        getSupportDelegate().startForResult(new SelectAddressDelegate(),1);

    }

    @OnClick(R2.id.btn_order_confirm_pay)
    void onClickPay(){
        FastPay.create(OrderConfirmDelegate.this)
                .setOrderId(orderNo)
                .setOrderAmount(orderAmount)
                .setPayResultListener(OrderConfirmDelegate.this)
                .beginPayDialog();
        getSupportDelegate().pop();
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK ) {

            final int addressId = data.getInt("addressId");
            final String receiver = data.getString("name");
            final String phone = data.getString("phone");
            final String address = data.getString("address");

            //通知服务器更新地址
            RestClient.builder()
                    .url("user/order/update_order_address.do")
                    .params("orderNo",orderNo)
                    .params("addressId",addressId)
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            final int status = JSON.parseObject(response).getInteger("status");
                            final String msg = JSON.parseObject(response).getString("msg");
                            switch (status){
                                case 0:
                                    Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                    mReceiver.setText(receiver);
                                    mPhone.setText(phone);
                                    mAddress.setText(address);
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
    }


    public static OrderConfirmDelegate create(long orderNo,double orderAmount){
        final Bundle args = new Bundle();
        args.putLong("orderNo", orderNo);
        args.putDouble("orderAmount", orderAmount);
        final OrderConfirmDelegate delegate = new OrderConfirmDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            orderNo = args.getLong("orderNo");
            orderAmount = args.getDouble("orderAmount");
        }
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_order_confirm;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        mConfirmFrameLayout.removeView(mSelectFrameLayout);
        mConfirmFrameLayout.removeView(mAddressFrameLayout);
        initData();
    }

    private void initData() {
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
                                final Double totalAmount = data.getDouble("totalAmount");
                                final int statusId = data.getInteger("statusId");

                                final Double productTax = data.getDouble("productTax");
                                final Double totalProductPrice = productPrice*quantity;
                                final Double totalProductTax = productTax*quantity;

                                final String createTime = data.getString("createTime");
                                final String updateTime = data.getString("updateTime");
                                final String payTime = data.getString("payTime");
                                final String sendTime = data.getString("dispatchTime");
                                final String receiptTime = data.getString("receiptTime");
                                final String closeTime = data.getString("closeTime");

                                final String address = data.getString("address");
                                if (address!= null){
                                    String[] addressArray = address.split(",");

                                    mConfirmFrameLayout.addView(mAddressFrameLayout);
                                    mReceiver.setText(addressArray[0]);
                                    mPhone.setText(addressArray[1]);
                                    mAddress.setText(addressArray[2]);
                                }else {
                                    mConfirmFrameLayout.addView(mSelectFrameLayout);
                                }

                                mSeller.setText(sellerUsername);
                                GlideApp.with(mImg)
                                        .load(imageHost+productImage)
                                        .into(mImg);
                                mTitle.setText(productName);
                                mQuantity.setText(String.valueOf(quantity));
                                mPrice.setText(String.valueOf(productPrice));
                                mTotalPrice.setText(String.valueOf(totalAmount));
                                mPayPrice.setText(String.valueOf(totalAmount));

                                mTotalProductPrice.setText(String.valueOf(totalProductPrice));
                                mTotalTaxPrice.setText(String.valueOf(totalProductTax));

                                mOrderNo.setText(String.valueOf(orderNo));
                                mCreateTime.setText(String.valueOf(createTime));

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
