package com.yhl.higo.ec.main.personal.order;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yhl.higo.delegates.HigoDelegate;
import com.yhl.higo.ec.R;
import com.yhl.higo.ec.R2;
import com.yhl.higo.ec.identify.IdentifyDelegate;
import com.yhl.higo.ec.identify.IdentifyItemType;
import com.yhl.higo.ec.main.personal.address.SelectAddressDelegate;
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
 * Created by Administrator on 2018/6/15/015.
 */

public class OrderSubmitDelegate extends HigoDelegate implements IAlPayResultListener {

    private int mAddressId;
    private String mOrderImgUrl;
    private String mOrderTitle;
    private Double mOrderPrice;
    private int mOrderQuantity;
    private double mTotalOrderPrice;
    private int mProductId = -1;
    private AlertDialog mDialog;
    private double mTaxPrice;

    @BindView(R2.id.txt_order_submit_name)
    TextView mName = null;
    @BindView(R2.id.txt_order_submit_phone)
    TextView mPhone = null;
    @BindView(R2.id.txt_order_submit_address)
    TextView mAddress = null;
    @BindView(R2.id.img_order_submit)
    ImageView mImage = null;
    @BindView(R2.id.txt_order_submit_title)
    TextView mTitle = null;
    @BindView(R2.id.txt_order_submit_price)
    TextView mPrice = null;
    @BindView(R2.id.txt_order_submit_count)
    TextView mNum = null;
    @BindView(R2.id.txt_order_submit_price_total)
    TextView mTotalPrice = null;
    @BindView(R2.id.txt_order_submit_num)
    TextView mOrderNum = null;
    @BindView(R2.id.txt_order_submit_tax_price)
    TextView mTotalTaxPrice = null;

    @BindView(R2.id.frame_order_submit_layout)
    FrameLayout mConfirmFrameLayout = null;
    @BindView(R2.id.frame_order_submit_select_address)
    FrameLayout mSelectFrameLayout = null;
    @BindView(R2.id.frame_order_submit_address)
    FrameLayout mAddressFrameLayout = null;

    @OnClick(R2.id.icon_order_submit_back)
    void onClickBack(){
        getSupportDelegate().pop();
    }

    //选择地址
    @OnClick(R2.id.rl_order_submit_select_address)
   void onClickSelectAddress(){
        getSupportDelegate().startForResult(new SelectAddressDelegate(),1);
        mConfirmFrameLayout.removeView(mSelectFrameLayout);
        mConfirmFrameLayout.addView(mAddressFrameLayout);
    }
    //选择地址
    @OnClick(R2.id.frame_order_submit_address)
    void onClickReSelectAddress(){
        getSupportDelegate().startForResult(new SelectAddressDelegate(),1);
    }


    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK ) {

            mAddressId = data.getInt("addressId");
            mName.setText(data.getString("name"));
            mPhone.setText(data.getString("phone"));
            mAddress.setText(data.getString("address"));
        }
    }

    //减少购买商品数
   @OnClick(R2.id.img_reduce_order_submit_num)
   void onClickReduce(){
       int currentCount = Integer.parseInt(mNum.getText().toString());
       if (currentCount>1){
           currentCount--;
           mNum.setText(String.valueOf(currentCount));
           mOrderNum.setText(String.valueOf(currentCount));
           mTotalPrice.setText(String.valueOf(mOrderPrice*currentCount+mTaxPrice*currentCount));
       }else {
           Toast.makeText(getContext(),"该宝贝不能减少了哟~",Toast.LENGTH_SHORT).show();
       }

   }
   //增加购买商品数
   @OnClick(R2.id.img_increase_order_submit_num)
    void onClickIncrease(){
       int currentCount = Integer.parseInt(mNum.getText().toString());
       currentCount++;
       mNum.setText(String.valueOf(currentCount));
       mOrderNum.setText(String.valueOf(currentCount));
       mTotalPrice.setText(String.valueOf(mOrderPrice*currentCount+mTaxPrice*currentCount));
    }
    //提交代购订单
    @OnClick(R2.id.btn_order_submit)
    void onClickConfirm(){
        RestClient.builder()
                .url("user/order/create_sell_order.do")
                .params("productId",mProductId)
                .params("productQuantity",Integer.parseInt(mNum.getText().toString()))
                .params("addressId",mAddressId)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        HigoLogger.d("CREATE_ORDER",response);
                        final int status = JSON.parseObject(response).getInteger("status");
                        switch (status){
                            case 0:
                                Toast.makeText(getContext(),"您已创建订单成功，快去订单支付吧",Toast.LENGTH_SHORT).show();
                                getSupportDelegate().pop();
                                break;
                            case 3:
                                final String need_login = JSON.parseObject(response).getString("msg");
                                Toast.makeText(getContext(),need_login,Toast.LENGTH_SHORT).show();
                                getSupportDelegate().start(new SignInCodeDelegate());
                                break;
                            default:
                                final String msg = JSON.parseObject(response).getString("msg");
                                Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
                                break;
                        }

                    }
                })
                .build()
                .post();

    }

    public static OrderSubmitDelegate create(int productId, String imgUrl, String title, Double price, int quantity ,double taxPrice) {

        final Bundle args = new Bundle();
        args.putInt("productId",productId);
        args.putString("imgUrl",imgUrl);
        args.putString("title",title);
        args.putDouble("price",price);
        args.putInt("quantity",quantity);
        args.putDouble("taxPrice",taxPrice);
        final OrderSubmitDelegate delegate = new OrderSubmitDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_order_submit;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View rootView) {
        final Bundle args = getArguments();
        if (args != null) {
            mOrderImgUrl = args.getString("imgUrl");
            mOrderTitle = args.getString("title");
            mOrderPrice = args.getDouble("price");
            mOrderQuantity = args.getInt("quantity");
            mProductId = args.getInt("productId");
            mTaxPrice = args.getDouble("taxPrice");

            GlideApp.with(getContext())
                    .load(mOrderImgUrl)
                    .into(mImage);

            mTitle.setText(mOrderTitle);
            mPrice.setText(String.valueOf(mOrderPrice));
            mNum.setText(String.valueOf(mOrderQuantity));
            mOrderNum.setText(String.valueOf(mOrderQuantity));
            mTotalTaxPrice.setText(String.valueOf(mTaxPrice*mOrderQuantity));
            mTotalPrice.setText(String.valueOf(mOrderPrice*mOrderQuantity+mTaxPrice*mOrderQuantity));
        }
        mConfirmFrameLayout.removeView(mAddressFrameLayout);
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
